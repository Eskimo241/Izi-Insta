package com.example.izyinsta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Callback;
import java.io.IOException;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private List<MediaItem> mediaItems;
    String servUrl = "https://android.chocolatine-rt.fr/androidServ/";

    //Constructeur
    public MediaAdapter(List<MediaItem> mediaItems) {
        this.mediaItems = mediaItems;
    }

    //Créer une nouvelle "vue" (ViewHolder) représentant un élément de la liste (mediaItems contenant nos images / Gifs)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
        return new ViewHolder(view);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView displayText;
        ImageView imageView;
        ImageView likeIcon;
        TextView nbOfLikes;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            displayText = itemView.findViewById(R.id.descImageView);
            imageView = itemView.findViewById(R.id.imageView);
            likeIcon = itemView.findViewById(R.id.likeImageView);
            nbOfLikes = itemView.findViewById(R.id.nbLikesImageView);
        }
    }

    //Met à jour le contenu d'une vue (ViewHolder) en fonction des données à la position donnée
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaItem mediaItem = mediaItems.get(position);
        Uri mediaUri = mediaItem.getUri();
        String mediaNormalUrl = mediaItem.getNormalUrl();

        if (mediaItem.getUri() != null) {
            // Cas : image ajoutée par l'utilisateur (URI)
            Glide.with(holder.imageView.getContext())
                    .load(mediaUri)
                    .into(holder.imageView);
        } else if (mediaItem.getNormalUrl() != null) {
            // Cas : image provenant du serveur (normalURL)
            Glide.with(holder.imageView.getContext())
                    .load(mediaNormalUrl)
                    .placeholder(R.drawable.ic_launcher_background) // Placeholder en cas de chargement
                    .error(R.drawable.ic_launcher_foreground) // Image d'erreur en cas de problème
                    .fitCenter() // Permet de centrer l'image dans le ImageView
                    .into(holder.imageView);
        }

        // Afficher le nom de l'image et l'utilisateur
        String description = mediaItem.imageName + " - publié par " + mediaItem.userCreator;
        holder.displayText.setText(description);


        holder.likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("DBG", "MediaId : "+mediaItem.getImageId());
                OkHttpClient client = new OkHttpClient.Builder()
                        .hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return hostname.equals("android.chocolatine-rt.fr") || hostname.endsWith(".eu.ngrok.io");
                            }
                        })
                        .build();

                SharedPreferences preferences = holder.likeIcon.getContext().getSharedPreferences("com.example.izyinsta.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
                String savedUsername = preferences.getString("username", "");
                if (savedUsername.equals("")) {
                    Log.d("DBG", "No username saved");
                    Intent intent = new Intent(holder.likeIcon.getContext(), Home.class);
                    intent.putExtra("error_message", "Une erreur s'est produite, veuillez vous reconnecter");

                    holder.likeIcon.getContext().startActivity(intent);
                    return;
                }

                Log.d("DBG", "savedUsername: "+savedUsername);


                RequestBody body = new FormBody.Builder()
                        .add("username", savedUsername)
                        .add("mediaId", mediaItem.getImageId().toString())
                        .build();

                Request request = new Request.Builder()
                        .url(servUrl + "like.php")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.d("DBG", "onFailure: "+e.getMessage());
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {

                            assert response.body() != null;
                            String myResponse = response.body().string();
                            Log.d("DBG", "MediaAdapterLikeClicked: "+myResponse);

                            //-----------------On update le nombre de like sur l'image-------------------------------

                            if(holder.likeIcon.getContext() instanceof Activity) {
                                Activity activity = (Activity) holder.likeIcon.getContext();
                                //Comme on est une "sous activité", mais qu'on veut faire des trucs de MainThread on utiliser l'activité du contexte
                                //c'est pas clair même dans ma tête
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            JSONObject obj = new JSONObject(myResponse);
                                            String likeCount = obj.getString("likeCount");
                                            holder.nbOfLikes.setText(likeCount);
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                            }







                        }
                    }
                });



            }

        });
        String likes = mediaItem.likes.toString();
        holder.nbOfLikes.setText(likes);

    }

    //Pour savoir combien d'éléments on doit afficher
    @Override
    public int getItemCount() {
        return mediaItems.size();
    }





}
