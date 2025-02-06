package com.example.izyinsta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Tendency extends AppCompatActivity {

    //RecyclerView recyclerView = findViewById(R.id.profilImgScroller);
    //recyclerView.setLayoutManager(new LinearLayoutManager(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tendency);

        //---Images et Gifs---------------------------------------------------------------------

        RecyclerView recyclerView = findViewById(R.id.tendencyImgScroller);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ... (Récupération des données depuis le serveur à faire) ...

        //Puis utilisation de retrofit2 pour récupérer et afficher les images / Gifs via MediaAdapter :

        //call.enqueue(new Callback<List<MediaItem>>() {
        //    @Override
        //    public void onResponse(Call<List<MediaItem>> call, Response<List<MediaItem>> response) {
        //        if (response.isSuccessful()) {
        //            List<MediaItem> mediaItems = response.body();
        //            MediaAdapter adapter = new MediaAdapter(mediaItems);
        //            recyclerView.setAdapter(adapter);
        //        } else {
        //        // Gérer les erreurs
        //        }
        //    }

        //    @Override
        //    public void onFailure(Call<List<MediaItem>> call, Throwable t) {
        //        // Gérer les erreurs
        //    }

        //-------------------------------------------------------------------------------------

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