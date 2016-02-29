package fr.dabernat.dimchat.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Utilisateur on 08/02/2016.
 */
public class User {


    private int userID;

    @SerializedName("identifiant")
    protected String pseudo;

    @SerializedName("lastactivity")
    private int lastActivity;

    private String imageUrl;

    public User() {
    }

    public User(int userID, String pseudo, String imageUrl) {
        this.userID = userID;
        this.pseudo = pseudo;
        this.imageUrl = imageUrl;
    }

    public User(int userID, String pseudo, int lastActivity, String imageUrl) {
        this.userID = userID;
        this.pseudo = pseudo;
        this.lastActivity = lastActivity;
        this.imageUrl = imageUrl;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(int lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
