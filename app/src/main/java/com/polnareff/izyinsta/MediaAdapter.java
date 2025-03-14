package com.polnareff.izyinsta;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private final List<MediaItem> mediaItems;
    private static final String servUrl = Constants.SERV_URL;

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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView displayText;
        TextView displayText2;
        ImageView imageView;
        ImageView likeIcon;
        TextView nbOfLikes;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            displayText = itemView.findViewById(R.id.descImageView);
            displayText2 = itemView.findViewById(R.id.descImageView2);
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
        String description = mediaItem.imageName + " - publié par ";
        String description2 = "" + mediaItem.userCreator;
        holder.displayText.setText(description);
        holder.displayText2.setText(description2);

        updateLikeIcon(holder.likeIcon, mediaItem.getHasLiked()>0); // Initialisation de l'icône

        holder.likeIcon.setOnClickListener(v -> {

            Log.d("DBG", "MediaId : "+mediaItem.getImageId());
            OkHttpClient client = Constants.getHttpClient();


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
                    Log.d("DBG", "onResponse: "+response);
                    if (response.isSuccessful()) {

                        assert response.body() != null;
                        String myResponse = response.body().string();
                        Log.d("DBG", "MediaAdapterLikeClicked: "+myResponse);

                        //-----------------On update le nombre de like sur l'image-------------------------------

                        if(holder.likeIcon.getContext() instanceof Activity) {
                            Activity activity = (Activity) holder.likeIcon.getContext();
                            //Comme on est une "sous activité", mais qu'on veut faire des trucs de MainThread on utiliser l'activité du contexte
                            //c'est pas clair même dans ma tête
                            activity.runOnUiThread(() -> {
                                try {
                                    JSONObject obj = new JSONObject(myResponse);
                                    int likeCount = obj.getInt("likeCount"); // Récupérer le nombre de likes
                                    boolean hasLiked = obj.getBoolean("hasLiked"); // Récupérer si l'utilisateur a liké (pour l'icône)
                                    mediaItem.likes = likeCount; // Mettre à jour le nombre de likes dans MediaItem
                                    holder.nbOfLikes.setText(String.valueOf(likeCount)); // Mettre à jour le TextView
                                    updateLikeIcon(holder.likeIcon, hasLiked); // Mettre à jour l'icône
                                    updateLikeColor(holder.nbOfLikes, hasLiked); // Mettre à jour le texte
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }







                    }
                }
            });



        });

        holder.displayText2.setOnClickListener(v -> {
            Intent intent = new Intent(holder.displayText2.getContext(), Profil.class);
            intent.putExtra("username", mediaItem.userCreator);
            holder.displayText2.getContext().startActivity(intent);
        });

        holder.imageView.setOnClickListener(v -> {
            Toast.makeText(holder.imageView.getContext(), "WIP", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(holder.imageView.getContext(), MediaDetail.class);
            //intent.putExtra("mediaItem", mediaItem);
            //holder.imageView.getContext().startActivity(intent);
        });

        String likes = mediaItem.likes.toString();
        holder.nbOfLikes.setText(likes);
        updateLikeColor(holder.nbOfLikes, mediaItem.getHasLiked() > 0); // Initialisation de la couleur

    }

    private void updateLikeIcon(ImageView likeIcon, boolean hasLikes) {
        likeIcon.setImageResource(hasLikes ? R.drawable.icons8_heart_100_default : R.drawable.icons8_heart_100_black);
    }

    private void updateLikeColor(TextView nbOfLikes, boolean hasLikes) {
        int color = hasLikes ? R.color.liked_color : R.color.black;
        nbOfLikes.setTextColor(ContextCompat.getColor(nbOfLikes.getContext(), color));
    }

    //Pour savoir combien d'éléments on doit afficher
    @Override
    public int getItemCount() {
        return mediaItems.size();
    }
}
