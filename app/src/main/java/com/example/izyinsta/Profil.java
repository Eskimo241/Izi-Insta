package com.example.izyinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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



import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.util.Base64;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


public class Profil extends AppCompatActivity {

    ImageView profilPicture;
    TextView displayName;
    ActivityResultLauncher<Intent> launchSomeActivity;
    String servUrl = "https://android.chocolatine-rt.fr/androidServ/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);


        Button buttonDisconnect = findViewById(R.id.buttonLogOff);

        //-------------------------------------------------------------------------------------------
        //---Select Profil Picture / Send to Server / Load from Server-------------------------------------------
        profilPicture = findViewById(R.id.profilPicture);
        displayName = findViewById(R.id.displayName);
        SharedPreferences preferences = getSharedPreferences("com.example.izyinsta.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);

        String savedUsername = preferences.getString("username", "");

        if(savedUsername.equals("")) {
            Log.e("DBG", "uploadImageToServer: No username found in shared preferences");
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("error_message", "Une erreur s'est produite, veuillez vous reconnecter");

            startActivity(intent);
            return;
        }
        displayName.setText(savedUsername);


        launchSomeActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            try {
                                //Alors je met un commentaire ici, mais en vrai c'est Pierre qui a fait ça, j'ai pas moyen d'expliquer ni comprendre
                                Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                                //On fait l'upload de l'image avec le nom d'utilisateur pour faire le lien dans le BDD
                                uploadImageToServer(selectedImageBitmap, savedUsername);
                                profilPicture.setImageBitmap(selectedImageBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        if(preferences.getString("pfp", "").equals("")) {
            loadProfilePicture(savedUsername);
        }
        else {
            String image = preferences.getString("pfp", "");
            byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profilPicture.setImageBitmap(decodedByte);
        }

        //loadProfilePicture(savedUsername);


        profilPicture.setOnClickListener(v -> imageChooser());

    }


    public void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }

    //$Partie d'envoi vers le serveur :
    private void uploadImageToServer(Bitmap bitmap, String savedUsername) {



        //Blabla on fait le client et la requête
        OkHttpClient client = new OkHttpClient();
        String encodedImage = encodeImage(bitmap);

        RequestBody body = new FormBody.Builder()
                //On fait un POST avec l'image encodée en b64
                .add("image", encodedImage)
                .add("username", savedUsername)
                .build();

        //On envoie la requête
        Request request = new Request.Builder()
                .url(servUrl + "uploadPfp.php")
                .post(body)
                .build();

        Log.d("DBG", "fetchPost: "+request);
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.d("fetchPost", "onFailure: "+e.getMessage());
            }

            @Override
            //Ici pour le coup on a pas besoin de réponse, un peu la flemme de faire qqchose
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("DBG", "onResponse: "+response);
                    SharedPreferences preferences = getSharedPreferences("com.example.izyinsta.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("pfp", encodedImage);

                    editor.apply();
                }


            }
        });

    }

    //Convertit l'image en une chaîne de caractères pour l'envoyer dans la requête POST:
    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    // ... (autres méthodes)



    //Partie sur la récupération de l'image du Serveur jusqu'à l'appli :
    private void loadProfilePicture(String savedUsername) {
        Log.d("TIME", "Start Load");

        OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return hostname.equals("android.chocolatine-rt.fr") || hostname.endsWith(".eu.ngrok.io");
                    }
                })
                .build();
        if(savedUsername.equals("")) {
            Log.e("DBG", "uploadImageToServer: No username found in shared preferences");
            return;
        }
        //On fait une requête, on a besoin que du nom d'utilisateur pour le serveur
        RequestBody body = new FormBody.Builder()
                .add("username", savedUsername)
                .build();

        Request request = new Request.Builder()
                .url(servUrl + "loadPfp.php")
                .post(body)
                .build();

        Log.d("DBG", "fetchPost: "+request);
        Log.d("TIME", "Fetching Image");
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.d("fetchPost", "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //Le serveur répond une image encodée en b64 dans le format JSON avec le clef "image"
                if (response.isSuccessful()) {
                    Log.d("TIME", "Response Received");

                    assert response.body() != null;
                    //Ici on récup le corps de la réponse, entre autre le contenu de la page/ce qui nous intéresse fortement (j'ai pas du tout passé 20min à chercher le pb alors que ct ça qui me manquait)
                    final String myResponse = response.body().string();

                    Profil.this.runOnUiThread(() -> {
                        try {
                            //On convertit la réponse en objet JSON qu'on peut manipuler facilement
                            JSONObject obj = new JSONObject(myResponse);

                            //On récup l'image encodée en b64
                            String image = obj.getString("image");
                            if (image.equals("default")) {
                                return;
                            }
                            SharedPreferences preferences = getSharedPreferences("com.example.izyinsta.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("pfp", image);

                            editor.apply();


                            byte[] decodedString = Base64.decode(image, Base64.DEFAULT);//Gépéto, on décode l'image

                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); //Gépéto, on convertit l'image en Bitmap
                            Log.d("TIME", "Image Decoded");
                            profilPicture.setImageBitmap(decodedByte); //On change l'image du profil avec l'image récupérée


                            Log.d("TIME", "Image Updated");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
                else {
                    Log.d("fetchPost", "onResponse: "+response.toString());
                }


            }
        });

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

    //Redirection vers les autres pages de l'appli
    public void toTendencyPage(View v) {
        Intent intent = new Intent(this, Tendency.class);
        startActivity(intent);
    }
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
}