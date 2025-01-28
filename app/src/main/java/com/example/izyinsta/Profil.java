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

        //---Scroll from one page to another------------------------------

        ImageView myHomeIcon = findViewById(R.id.profilHomeIcon);

        final Profil profil = this;
        myHomeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profil, Tendency.class);
                startActivity(intent);
            }
        });

        Button buttonDisconnect = findViewById(R.id.buttonLogOff);

    }
    public void logout(View view) {
        SharedPreferences preferences = getSharedPreferences("com.example.izyinsta.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Context context = getApplicationContext();
        Intent intent = new Intent(context, Home.class);
        startActivity(intent);
    }

    public void profilSelect(View v) {
        devSend devSendActivity = new devSend();
        devSendActivity.imageChooser(v);
    }
}