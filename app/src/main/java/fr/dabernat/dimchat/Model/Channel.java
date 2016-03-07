package fr.dabernat.dimchat.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Channel implements Serializable {

    private int channelID;
    private String name;

    @SerializedName("connectedusers")
    private int connectedUsers;

    public Channel(int channelID, String name, int connectedUsers) {
        this.channelID = channelID;
        this.name = name;
        this.connectedUsers = connectedUsers;
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

    public int getConnectedUsers() {
        return connectedUsers;
    }

    public void setConnectedUsers(int connectedUsers) {
        this.connectedUsers = connectedUsers;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelID=" + channelID +
                ", name='" + name + '\'' +
                ", connectedUsers=" + connectedUsers +
                '}';
    }
}
