package com.example.izyinsta;

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

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private List<MediaItem> mediaItems;

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
