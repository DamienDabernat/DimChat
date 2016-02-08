package fr.dabernat.dimchat.model;

import java.io.Serializable;

/**
 * Created by Utilisateur on 02/02/2016.
 */
public class CurrentUser extends User implements Serializable {

    public String password;
    public String token;

    public CurrentUser() {
    }

    public CurrentUser(String pseudo, String password) {
        this.pseudo = pseudo;
        this.password = password;
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

    @Override
    public String toString() {
        return "CurrentUser{" +
                "pseudo='" + pseudo + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
