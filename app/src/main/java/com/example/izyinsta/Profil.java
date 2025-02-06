package com.example.izyinsta;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.content.Context;
import android.content.SharedPreferences;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.io.IOException;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class Profil extends AppCompatActivity {

    ImageView profilPicture;
    ActivityResultLauncher<Intent> launchSomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        //---Scroll from one page to another--------------------------------------------------------

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

        //-------------------------------------------------------------------------------------------
        //---Select Profil Picture / Send to Server / Load from Server-------------------------------------------
        profilPicture = findViewById(R.id.profilPicture);

        launchSomeActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            try {
                                Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                                profilPicture.setImageBitmap(selectedImageBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        profilPicture.setOnClickListener(v -> imageChooser());

    }

    public void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }

    //$Partie d'envoi vers le serveur :
    private void uploadImageToServer(Bitmap bitmap, final int userId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://android.chocolatine-rt.fr:7217/androidServ/uploadImage.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Profil.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Profil.this, "Error uploading image: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            // Pour envoyer l'image encodée et l'ID de l'utilisateur au serveur.
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String imageData = encodeImage(bitmap);
                params.put("image", imageData);
                params.put("userId", String.valueOf(userId)); // Envoie l'ID de l'utilisateur
                return params;
            }
        };

        queue.add(stringRequest);
    }

    //Convertit l'image en une chaîne de caractères pour l'envoyer dans la requête POST.
    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    // ... (autres méthodes)

    private void saveProfilePicture(Bitmap bitmap, int userId) {
        uploadImageToServer(bitmap, userId);
    }

    //$Partie sur la récupération de l'image du Serveur jusqu'à l'appli :
    private void loadProfilePicture(int userId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://android.chocolatine-rt.fr:7217/androidServ/loadImage.php" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null && !response.isEmpty()) {
                            try {
                                byte[] imageBytes = Base64.decode(response, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                profilPicture.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // Gérer les erreurs de décodage
                            }
                        } else {
                            // L'utilisateur n'a pas encore d'image de profil
                            profilPicture.setImageResource(R.drawable.icons8_account_96); // Image par défaut
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Gérer les erreurs de réseau
                        Toast.makeText(Profil.this, "Error loading profile picture: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(stringRequest);
    }

    //----------------------------------------------------------------------------------------------

    public void logout(View view) {
        SharedPreferences preferences = getSharedPreferences("com.example.izyinsta.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Context context = getApplicationContext();
        Intent intent = new Intent(context, Home.class);
        startActivity(intent);
    }
}