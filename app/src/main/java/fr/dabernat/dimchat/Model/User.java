package fr.dabernat.dimchat.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Utilisateur on 08/02/2016.
 */
public class User implements Serializable {


    private int userID;

    @SerializedName("identifiant")
    protected String pseudo;

    @SerializedName("lastactivity")
    private String lastActivity;

    private String imageUrl;

    public User() {
    }

    public User(int userID, String pseudo, String imageUrl) {
        this.userID = userID;
        this.pseudo = pseudo;
        this.imageUrl = imageUrl;
    }

    public User(int userID, String pseudo, String lastActivity, String imageUrl) {
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

    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
