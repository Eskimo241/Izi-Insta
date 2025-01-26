package com.example.izyinsta;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.content.Context;
import android.content.SharedPreferences;


public class Profil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        ImageView myHomeIcon = findViewById(R.id.profilHomeIcon);

        final Profil profil = this;
        myHomeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profil, Tendency.class);
                startActivity(intent);
            }
        });

        Button buttonDeconnect = findViewById(R.id.buttonLogOff);
        buttonDeconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("com.example.izyinsta.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                Context context = getApplicationContext();
                Intent intent = new Intent(context, Home.class);
                context.startActivity(intent);
            }
        });
    }
}