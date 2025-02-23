package com.example.izyinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Search extends AppCompatActivity {
    private static final String servUrl = Constants.SERV_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        fetch("");


        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.setFocusableInTouchMode(true);
        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("DBG", "onTextChanged: " + s.toString());
                fetch(s.toString());
                // Implement your filtering logic here
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });







    }

    public void fetch(String search) {
        SharedPreferences preferences = getSharedPreferences("com.example.izyinsta.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        String savedUsername = preferences.getString("username", "");
        if (savedUsername.equals("")) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }
        RecyclerView recyclerView = findViewById(R.id.searchImgScroller);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        OkHttpClient client = Constants.getHttpClient();

        //On fait une requête, on a besoin que du nom d'utilisateur pour le serveur
        RequestBody body = new FormBody.Builder()
                .add("username", savedUsername)
                .add("search", search)
                .build();

        Request request = new Request.Builder()
                .url(servUrl + "search.php")

                .post(body)
                .build();

        Log.d("DBG", "fetchPost: "+request);
        client.newCall(request).enqueue(new okhttp3.Callback() {


            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.d("DBG", "onFailure: "+e.getMessage());
                Toast.makeText(Search.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
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
                    Search.this.runOnUiThread(() -> {
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
                                        servUrl+"addImg/"+normalUrl,
                                        servUrl+"addImg/"+tinyUrl,
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


    //Redirection vers les autres pages de l'appli
    public void toTendencyPage(View v) {
        Intent intent = new Intent(this, Tendency.class);
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