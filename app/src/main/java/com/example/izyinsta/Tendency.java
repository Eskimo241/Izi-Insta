package com.example.izyinsta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.Reference;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tendency extends AppCompatActivity {

    //RecyclerView recyclerView = findViewById(R.id.profilImgScroller);
    //recyclerView.setLayoutManager(new LinearLayoutManager(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tendency);

        //---Images et Gifs---------------------------------------------------------------------

        RecyclerView recyclerView = findViewById(R.id.profilImgScroller);
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

        ImageView myProfilIcon = findViewById(R.id.tendencyProfilIcon);

        final Tendency tendency = this;
        myProfilIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(tendency, Profil.class);
                startActivity(intent);
            }
        });

    }
    public void goSend(View v) {
        Intent intent = new Intent(this, devSend.class);
        startActivity(intent);
    }
}