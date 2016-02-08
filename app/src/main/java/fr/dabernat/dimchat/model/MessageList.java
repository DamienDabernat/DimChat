package fr.dabernat.dimchat.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Utilisateur on 08/02/2016.
 */
public class MessageList {

    @SerializedName("messages")
    private List<Message> messageList;

    public MessageList() {
    }

    public MessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public String toString() {
        return "MessageList{" +
                "messageList=" + messageList +
                '}';
    }
}
