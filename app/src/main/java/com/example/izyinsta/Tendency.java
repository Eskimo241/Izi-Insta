package com.example.izyinsta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Tendency extends AppCompatActivity {

    //RecyclerView recyclerView = findViewById(R.id.profilImgScroller);
    //recyclerView.setLayoutManager(new LinearLayoutManager(this));
    String servUrl = "https://android.chocolatine-rt.fr/androidServ/";
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tendency);

        //---Images et Gifs---------------------------------------------------------------------







        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::fetch);
        fetch();



    }

    public void fetch() {

        RecyclerView recyclerView = findViewById(R.id.tendencyImgScroller);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences preferences = getSharedPreferences("com.example.izyinsta.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        String username = preferences.getString("username", "");
        OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return hostname.equals("android.chocolatine-rt.fr") || hostname.endsWith(".eu.ngrok.io");
                    }
                })
                .build();
        //On fait une requête, on a besoin que du nom d'utilisateur pour le serveur
        RequestBody body = new FormBody.Builder()
                .add("username", username )
                .build();

        Request request = new Request.Builder()
                .url(servUrl + "tendency.php")
                .post(body)
                .build();

        Log.d("DBG", "fetchPost: "+request);
        client.newCall(request).enqueue(new okhttp3.Callback() {


            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.d("DBG", "onFailure: "+e.getMessage());
                Toast.makeText(Tendency.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                //-----------On récupère tout les attributs de la table Images-------------------:
                //mediaId, imageName, tinyUrl, normalUrl, likes, date
                Log.d("DBG", "onResponse: "+response);
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseStr = response.body().string();
                    Log.d("DBG", "onResponse: "+responseStr);
                    //-----------Ici on a les données comme une liste de JSON-------------------:
                    //[{"mediaId":1,"imageName":...},{"mediaId":2,"imageName":...},...]
                    Tendency.this.runOnUiThread(() -> {
                        try {
                            JSONArray mediaItemsJson = new JSONArray(responseStr); // Réponse JSON contenant un tableau d'images
                            List<MediaItem> mediaItems = new ArrayList<>(); // Liste pour stocker les objets MediaItem
                            for (int i = 0; i < mediaItemsJson.length(); i++) {
                                JSONObject mediaItemJson = mediaItemsJson.getJSONObject(i);

                                //Log.d("DBG", "Media: "+mediaItemJson.toString());
                                //-----------Ici on sépare les données à chaque itération-------------------:
                                //{"mediaId":1,"imageName":...}
                                //{"mediaId":2,"imageName":...}

                                Integer mediaId = mediaItemJson.getInt("mediaId");
                                String imageName = mediaItemJson.getString("imageName");
                                String normalUrl = mediaItemJson.getString("normalUrl");
                                String tinyUrl = mediaItemJson.getString("tinyUrl");
                                Integer likes = mediaItemJson.getInt("likeCount");
                                String userCreator = mediaItemJson.getString("username");
                                String date = mediaItemJson.getString("date");
                                Integer hasLiked = mediaItemJson.getInt("hasLiked");

                                // Création de l'objet MediaItem avec les données récupérées
                                MediaItem mediaItem = new MediaItem(
                                        mediaId,
                                        imageName,
                                        "https://android.chocolatine-rt.fr/androidServ/addImg/"+normalUrl,
                                        "https://android.chocolatine-rt.fr/androidServ/addImg/"+tinyUrl,
                                        likes,
                                        null, // likeThisDay (à adapter si nécessaire)
                                        null, // isTrending (à adapter si nécessaire)
                                        userCreator,
                                        null, // hashtag (à adapter si nécessaire)
                                        date,
                                        null,
                                        hasLiked,
                                        null
                                );
                                mediaItems.add(mediaItem);
                            }

                            MediaAdapter mediaAdapter = new MediaAdapter(mediaItems);
                            recyclerView.setAdapter(mediaAdapter);
                            swipeRefreshLayout.setRefreshing(false);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    Log.d("DBG", "onResponse: "+response.message());
                }
            }
        });
    }
    //Pour tester l'envoi vers le serveur avec la classe devSend (remplacé par AddImage)
    public void goSend(View v) {
        Intent intent = new Intent(this, devSend.class);
        startActivity(intent);
    }

    //Redirection vers les autres pages de l'appli
    public void toSearchPage(View v) {
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }
    public void toAddImagePage(View v) {
        Intent intent = new Intent(this, AddImage.class);
        startActivity(intent);
    }
    public void toLikesPage(View v) {
        Intent intent = new Intent(this, Likes.class);
        startActivity(intent);
    }
    public void toProfilPage(View v) {
        Intent intent = new Intent(this, Profil.class);
        startActivity(intent);
    }
}