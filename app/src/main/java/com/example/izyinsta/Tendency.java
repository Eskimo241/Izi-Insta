package com.example.izyinsta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tendency);

        //---Images et Gifs---------------------------------------------------------------------

        RecyclerView recyclerView = findViewById(R.id.tendencyImgScroller);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

                                Log.d("DBG", "Media: "+mediaItemJson.toString());
                                //-----------Ici on sépare les données à chaque itération-------------------:
                                //{"mediaId":1,"imageName":...}
                                //{"mediaId":2,"imageName":...}

                                Integer mediaId = mediaItemJson.getInt("mediaId");
                                String imageName = mediaItemJson.getString("imageName");
                                String normalUrl = mediaItemJson.getString("normalUrl");
                                String tinyUrl = mediaItemJson.getString("tinyUrl");
                                Integer likes = mediaItemJson.getInt("likes");
                                String date = mediaItemJson.getString("date");

                                // Création de l'objet MediaItem avec les données récupérées
                                MediaItem mediaItem = new MediaItem(
                                        mediaId,
                                        imageName,
                                        normalUrl,
                                        tinyUrl,
                                        likes,
                                        null, // likeThisDay (à adapter si nécessaire)
                                        null, // isTrending (à adapter si nécessaire)
                                        null, // userCreator (à adapter si nécessaire)
                                        null, // hashtag (à adapter si nécessaire)
                                        date,
                                        null,
                                        null
                                );
                                mediaItems.add(mediaItem);
                            }

                            MediaAdapter mediaAdapter = new MediaAdapter(mediaItems);
                            recyclerView.setAdapter(mediaAdapter);

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