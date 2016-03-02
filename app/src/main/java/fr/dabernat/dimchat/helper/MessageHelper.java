package fr.dabernat.dimchat.helper;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.dabernat.dimchat.model.Message;

/**
 * Created by Utilisateur on 01/03/2016.
 */
public class MessageHelper {

    private static final String TAG = "MessageHelper";

    public static List<Message> getNewMessage(List<Message> oldList, List<Message> newList) {

        List<Message> newMessage = new ArrayList<>();

        if(oldList.size() > 0) {
//            Log.w(TAG, "getNewMessage: liste old taille : " + oldList.size() );

            Message lastOldMessage = oldList.get(oldList.size()-1);
            Message newLastMessage = newList.get(newList.size()-1);

//            Log.w(TAG, "getNewMessage: " + oldList.toString() );
//
//            Log.w(TAG, "getNewMessage: old last message" + lastOldMessage );
//            Log.w(TAG, "getNewMessage: new last message" + newLastMessage );

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
