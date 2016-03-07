package fr.dabernat.dimchat.helper;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.dabernat.dimchat.model.Message;

public class MessageHelper {

    private static final String TAG = "MessageHelper";

    public static List<Message> getNewMessage(List<Message> oldList, List<Message> newList) {

        List<Message> newMessage = new ArrayList<>();

        if(oldList.size() > 0) {

            Message lastOldMessage = oldList.get(oldList.size()-1);
            Message newLastMessage = newList.get(newList.size()-1);


            int lastIndex = newList.lastIndexOf(lastOldMessage);

            int numberOfNewItem = 14 - lastIndex;

//            Log.w(TAG, "getNewMessage: index : " + lastIndex );

            if(numberOfNewItem != 0) {
                int i;
                for (i = (newList.size())-numberOfNewItem; i < newList.size()-1; i++) {
                    newMessage.add(newList.get(i));
                    Log.w(TAG, "getNewMessage: new message" + newList.get(i).toString());
                }
            }

            return newMessage;
        }

        return newList;
    }

}
