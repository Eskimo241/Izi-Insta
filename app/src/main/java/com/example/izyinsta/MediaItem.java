package com.example.izyinsta;

import android.net.Uri;

//Dans la BDD :
//Images (mediaId,
//        imageName,
//        tinyUrl,
//        normalUrl,
//        likes,
//        likesThisDay,
//        isTrending,
//        userCreator,
//        date)
//et
//Users (userId, username, hashedpasswd, createDate, pfp, mail)

public class MediaItem {
    private Integer imageId;
    public String imageName;
    private String normalUrl; //Correspond aux images de taille normal
    private String tinyUrl; //Correspond aux images de petite taille
    public Integer likes; //Nombre de likes total
    private Integer likeThisDay; // Nombre de like aujourd'hui
    private Boolean isTrending; // Si l'image ou gif est en tendance (dépend du nombre de like aujourd'hui)
    public String userCreator; //Celui qui a publié l'image
    private String hashtag; // Mots-clé associés
    private String date; //Date de publication
    private String type; // Type de média (image ou gif)
    private Uri uri;

    //---Constructeur, getters et setters-----------------------------------------------------

    public MediaItem (Integer imageId,String imageName,String normalUrl,String tinyUrl,Integer likes,Integer likeThisDay,Boolean isTrending,String userCreator,String hashtag,String date,String type,Uri uri) {
        this.imageId = imageId;
        this.imageName = imageName;
        this.normalUrl = normalUrl;
        this.tinyUrl = tinyUrl;
        this.likes = likes;
        this.likeThisDay = likeThisDay;
        this.isTrending = isTrending;
        this.userCreator = userCreator;
        this.hashtag = hashtag;
        this.date = date;
        this.type = type;
        this.uri = uri;
    }

    public Integer getImageId() {
        return imageId;
    }
    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getNormalUrl() {
        return normalUrl;
    }
    public void setNormalUrl(String normalUrl) {
        this.normalUrl = normalUrl;
    }

    public String getTinyUrl() {
        return tinyUrl;
    }
    public void setTinyUrl(String tinyUrl) {
        this.tinyUrl = tinyUrl;
    }

    public Integer getLikes() {
        return likes;
    }
    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getLikeThisDay() {
        return likeThisDay;
    }
    public void setLikeThisDay(Integer likeThisDay) {
        this.likeThisDay = likeThisDay;
    }

    public Boolean getIsTrending() {
        return isTrending;
    }
    public void setIsTrending(Boolean isTrending) {
        this.isTrending = isTrending;
    }

    public String getUserCreator() {
        return userCreator;
    }
    public void setUserCreator(String userCreator) {
        this.userCreator = userCreator;
    }

    public String getHashtag() {
        return hashtag;
    }
    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Uri getUri() { return uri; }
    public void setUri(Uri uri) {
        this.uri = uri;
    }
    //----------------------------------------------------------------------------------------
}
