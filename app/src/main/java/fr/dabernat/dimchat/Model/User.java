package fr.dabernat.dimchat.Model;

import android.util.Log;

/**
 * Created by Utilisateur on 02/02/2016.
 */
public class User {

    public String pseudo;
    public String password;
    public String token;

    public User(String pseudo, String password) {
        this.pseudo = pseudo;
        this.password = password;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
