package fr.dabernat.dimchat.model;

import java.io.Serializable;

/**
 * Created by Utilisateur on 08/02/2016.
 */
public class Channel implements Serializable {

    private int channelID;
    private String name;
    private int conectedUsers;

    public Channel() {
    }

    public Channel(int channelID, String name, int conectedUsers) {
        this.channelID = channelID;
        this.name = name;
        this.conectedUsers = conectedUsers;
    }

    public int getChannelID() {
        return channelID;
    }

    public void setChannelID(int channelID) {
        this.channelID = channelID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getConectedUsers() {
        return conectedUsers;
    }

    public void setConectedUsers(int conectedUsers) {
        this.conectedUsers = conectedUsers;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelID=" + channelID +
                ", name='" + name + '\'' +
                ", conectedUsers=" + conectedUsers +
                '}';
    }
}
