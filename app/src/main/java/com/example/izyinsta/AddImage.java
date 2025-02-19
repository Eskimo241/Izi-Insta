package com.example.izyinsta;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class AddImage extends AppCompatActivity {

    private ActivityResultLauncher<Intent> launchSomeActivity;
    private List<MediaItem> mediaItems = new ArrayList<>();
    private MediaAdapter mediaAdapter;
    private RecyclerView addImgScroller;
    private Button addImage;
    String servUrl = "https://android.chocolatine-rt.fr/androidServ/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        addImgScroller = findViewById(R.id.addImgScroller);
        addImgScroller.setLayoutManager(new LinearLayoutManager(this));
        mediaAdapter = new MediaAdapter(mediaItems);
        addImgScroller.setAdapter(mediaAdapter);

        SharedPreferences preferences = getSharedPreferences("com.example.izyinsta.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        String savedUsername = preferences.getString("username", "");

        // ... initialisation du RecyclerView et de l'adapter
        launchSomeActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {

                            Uri selectedImageUri = data.getData(); // Récupérer l'URI du fichier
                            String type = getMediaType(selectedImageUri); // Récupérer l'extension du fichier
                            String fileName = getFileNameFromUri(selectedImageUri); // Récupérer le nom du fichier d'origine

                            MediaItem mediaItem = new MediaItem(
                                    null, // imageId (à adapter)
                                    fileName, // imageName (à adapter)
                                    null, // normalUrl (à adapter)
                                    null, // tinyUrl (à adapter)
                                    null, // likes (à adapter)
                                    null, // likeThisDay (à adapter)
                                    null, // isTrending (à adapter)
                                    null, // userCreator (à adapter)
                                    null, // hashtag (à adapter)
                                    null, // date (à adapter)
                                    type, // type
                                    selectedImageUri // uri
                            );
                            mediaItems.add(mediaItem);
                            mediaAdapter.notifyItemInserted(mediaItems.size() - 1);
                            uploadUserImage(mediaItem, savedUsername, new UploadCallback() {
                                @Override
                                public void onSuccess() {
                                    // L'image a été téléchargée avec succès.
                                    // Mise à jour de l'historique :
                                    loadUserImages(savedUsername); // On recharge l'historique depuis le serveur
                                    addImgScroller.scrollToPosition(0); // On revient tout en haut du scroller
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Log.e("Upload Error", errorMessage);
                                }
                            });
                        }
                    }
                });

        addImage = findViewById(R.id.buttonAddImage);
        addImage.setOnClickListener(v -> imageChooser());

        //On charge une première fois l'historique depuis le serveur
        loadUserImages(savedUsername);
    }

    //Selectionner l'image depuis sa galerie
    public void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }

    // Récupérer l'extension du fichier
    private String getMediaType(Uri uri) {
        ContentResolver cR = getContentResolver();
        String type = cR.getType(uri);
        return type != null ? type : "image/jpeg"; // Type par défaut si non reconnu (image/jpeg est un bon choix courant)
    }

    // Récupérer le nom du fichier d'origine
    private String getFileNameFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            String fileName = cursor.getString(nameIndex);
            cursor.close();
            return fileName;
        }
        return null;
    }





    //---Partie d'envoi vers le serveur :-----------------------------------------------------------
    private void uploadUserImage(MediaItem mediaItem, String savedUsername,  UploadCallback callback) {

        //On fait une vérification, mais en vrai si ça fail, c'est qu'on est pas connecté...
        if(savedUsername.equals("")) {
            Log.e("DBG", "uploadImageToServer: No username found in shared preferences");
            callback.onFailure("No username found"); // Appeler le callback en cas d'erreur
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return hostname.equals("android.chocolatine-rt.fr") || hostname.endsWith(".eu.ngrok.io");
                    }
                })
                .build();

        try {
            String encodedImage = encodeImage(mediaItem.getUri(), mediaItem.getType());

            RequestBody body = new FormBody.Builder()
                    .add("image", encodedImage)
                    .add("username", savedUsername)
                    .add("imageName", mediaItem.getImageName()) // Envoyer le nom du fichier au serveur
                    .build();

            //On envoie la requête
            Request request = new Request.Builder()
                    .url(servUrl + "uploadUserImage.php")
                    .post(body)
                    .build();

            Log.d("DBG", "fetchPost: "+request);

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                    Log.d("fetchPost", "onFailure: "+e.getMessage());
                    callback.onFailure(e.getMessage()); // Appeler le callback en cas d'échec
                }

                @Override
                //Ici pour le coup on a pas besoin de réponse, un peu la flemme de faire qqchose
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {

                        // L'upload a réussi, mais on ne fait rien avec la réponse ici
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("DBG", "onResponse: "+response);
                                callback.onSuccess(); // Appeler le callback pour indiquer le succès

                            }
                        });
                    } else {
                        // ... (Gestion des erreurs de réponse du serveur - reste inchangée)
                        callback.onFailure("Erreur lors de l'upload: " + response.code());
                    }
                }
            });

        } catch (IOException e) {
            Log.e("DBG", "Erreur lors de l'encodage de l'image : " + e.getMessage());
        }
    }

    //Permet de vérifier les images ajoutées pour les charger en tant réel juste après l'ajout
    interface UploadCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    private String encodeImage(Uri uri, String type) throws IOException {
        //On lit d'abord les données du fichier avec InputStream
        ContentResolver contentResolver = getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(uri);

        if (inputStream != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            byte[] imageBytes = baos.toByteArray();
            inputStream.close();

            //On utilise un encodage différent pour les JPEG et les GIFs
            if (type.equals("gif")) {
                // Encodage Base64 pour les GIFs
                return Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } else {
                // Compression JPEG pour les images (ajuster la qualité si nécessaire)
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); // Qualité 80
                byte[] compressedBytes = baos.toByteArray();
                return Base64.encodeToString(compressedBytes, Base64.DEFAULT);
            }
        } else {
            throw new IOException("Impossible d'ouvrir le flux d'entrée pour l'URI : " + uri);
        }
    }





    //---Partie de récupération depuis le serveur :-------------------------------------------------
    private void loadUserImages(String savedUsername) {
        OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return hostname.equals("android.chocolatine-rt.fr") || hostname.endsWith(".eu.ngrok.io");
                    }
                })
                .build();

        if (savedUsername.equals("")) {
            Log.e("DBG", "loadUserImages: No username found in shared preferences");
            return;
        }

        RequestBody body = new FormBody.Builder()
                .add("username", savedUsername)
                .build();

        Request request = new Request.Builder()
                .url(servUrl + "loadUserImages.php")
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("loadUserImages", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //-----------On récupère tout les attributs de la table Images-------------------:
                //mediaId, imageName, tinyUrl, normalUrl, likes, date
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    final String myResponse = response.body().string();
                    Log.d("DBG", "onResponse: " + myResponse);
                    //-----------Ici on a les données comme une liste de JSON-------------------:
                    //[{"mediaId":1,"imageName":...},{"mediaId":2,"imageName":...},...]

                    AddImage.this.runOnUiThread(() -> {
                        try {
                            JSONArray mediaItemsJson = new JSONArray(myResponse); // Réponse JSON contenant un tableau d'images
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
                                Integer likes = mediaItemJson.getInt("likeCount");
                                String userCreator = mediaItemJson.getString("username");
                                String date = mediaItemJson.getString("date");

                                // Création de l'objet MediaItem avec les données récupérées
                                MediaItem mediaItem = new MediaItem(
                                        mediaId,
                                        imageName,
                                        "https://android.chocolatine-rt.fr/androidServ/addImg/"+normalUrl,
                                        "https://android.chocolatine-rt.fr/androidServ/addImg/"+tinyUrl,
                                        likes,
                                        null, // likeThisDay (à adapter si nécessaire)
                                        null, // isTrending (à adapter si nécessaire)
                                        userCreator, // userCreator (à adapter si nécessaire)
                                        null, // hashtag (à adapter si nécessaire)
                                        date,
                                        null,
                                        null
                                );
                                mediaItems.add(mediaItem);
                            }

                            MediaAdapter mediaAdapter = new MediaAdapter(mediaItems);
                            addImgScroller.setAdapter(mediaAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    Log.d("loadUserImages", "onResponse: " + response.toString());
                }
            }
        });
    }






    //---Redirection vers les autres pages de l'appli-----------------------------------------------
    public void toTendencyPage(View v) {
        Intent intent = new Intent(this, Tendency.class);
        startActivity(intent);
    }
    public void toSearchPage(View v) {
        Intent intent = new Intent(this, Search.class);
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