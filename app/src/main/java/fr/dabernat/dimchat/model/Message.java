package fr.dabernat.dimchat.model;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Comparable {

    private int userID;
    private int sendbyme = 0;
    private int everRead = 1;
    private String username;
    private String message;
    private String date;
    private String imageUrl;

    public Message() {
    }

    public Message(int userID, String username, String message, String date, String imageUrl) {
        this.userID = userID;
        this.username = username;
        this.message = message;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public int getSendbyme() {
        return sendbyme;
    }

    public void setSendbyme(int sendbyme) {
        this.sendbyme = sendbyme;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getEverRead() {
        return everRead;
    }

    public void setEverRead(int everRead) {
        this.everRead = everRead;
    }

    @Override
    public String toString() {
        return "Message{" +
                "userID=" + userID +
                ", username='" + username + '\'' +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object another) {
        Message message = (Message) another;

        if(this.getDate().equals(message.getDate()) && this.getUserID() == message.getUserID()){
            return 0;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(this.getDate());
            date2 = format.parse(message.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date1.compareTo(date2) <= 0) {
            System.out.println("dateString1 is an earlier date than dateString2");
            return -1;
        }

        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (userID != message1.userID) return false;
        if (username != null ? !username.equals(message1.username) : message1.username != null)
            return false;
        if (message != null ? !message.equals(message1.message) : message1.message != null)
            return false;
        if (date != null ? !date.equals(message1.date) : message1.date != null) return false;
        return imageUrl != null ? imageUrl.equals(message1.imageUrl) : message1.imageUrl == null;
    }

    @Override
    public int hashCode() {
        int result = userID;
        result = 31 * result + sendbyme;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        return result;
    }
}
