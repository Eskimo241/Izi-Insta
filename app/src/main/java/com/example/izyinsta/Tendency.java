package com.example.izyinsta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Tendency extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tendency);

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