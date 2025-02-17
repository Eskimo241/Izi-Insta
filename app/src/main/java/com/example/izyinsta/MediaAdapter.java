package com.example.izyinsta;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.View;
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

    }

    //Pour savoir combien d'éléments on doit afficher
    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }



}
