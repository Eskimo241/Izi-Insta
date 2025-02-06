package com.example.izyinsta;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;

import java.net.URL;
import java.util.Date;

import java.util.ArrayList;
import java.util.List;

public class AddImage extends AppCompatActivity {

    private ActivityResultLauncher<Intent> launchSomeActivity;
    private List<MediaItem> mediaItems = new ArrayList<>();
    private MediaAdapter mediaAdapter;
    private RecyclerView addImgScroller;
    private Button addImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        addImgScroller = findViewById(R.id.addImgScroller);
        addImgScroller.setLayoutManager(new LinearLayoutManager(this));
        mediaAdapter = new MediaAdapter(mediaItems);
        addImgScroller.setAdapter(mediaAdapter);

        // ... initialisation du RecyclerView et de l'adapter
        launchSomeActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            String type = getMediaType(selectedImageUri);

                            MediaItem mediaItem = new MediaItem(
                                    null, // imageId (à adapter)
                                    null, // imageName (à adapter)
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
                        }
                    }
                });
        addImage = findViewById(R.id.buttonAddImage);
        addImage.setOnClickListener(v -> imageChooser());
    }

    public void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }


    private String getMediaType(Uri uri) {
        ContentResolver cR = getContentResolver();
        String type = cR.getType(uri);
        if (type != null && type.startsWith("image/")) {
            return "image";
        } else if (type != null && type.equals("image/gif")) {
            return "gif";
        }
        return "image"; // Type par défaut si non reconnu
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
    public void toLikesPage(View v) {
        Intent intent = new Intent(this, Likes.class);
        startActivity(intent);
    }
    public void toProfilPage(View v) {
        Intent intent = new Intent(this, Profil.class);
        startActivity(intent);
    }

}