package fr.dabernat.dimchat.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.identity.intents.AddressConstants;

import java.util.Set;

import fr.dabernat.dimchat.ApplicationManager;
import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.activity.LoginActivity;

public class GCMBroadcastReceiver extends BroadcastReceiver {

    public GCMBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle args = intent.getExtras();
        String message = args.getString("message");
        Integer channelId = args.getInt("channelID");
        String username = args.getString("username");

        if(!message.isEmpty() && channelId != null && !username.isEmpty()) {
            showNotification(username, message, channelId);
        }
    }

    public void showNotification(String username, String message, int channelId) {

        Context context = ApplicationManager.getContext();

        Intent intentStreamingUI = new Intent(context, LoginActivity.class);
        //intentStreamingUI.setAction(MY_CUSTOM_ACTION);
        //intentStreamingUI.putExtra(EXTRA_MY_EXTRA,someExtraData);
        intentStreamingUI.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            intentStreamingUI.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intentStreamingUI,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context)
                .setContentTitle("Nouveau message")
                .setContentText(username + " : " + message)
                .setVibrate(new long[]{0, 200, 100, 200})
                //Set the color of the notification led (example on Nexus 5)
                .setLights(Color.parseColor("#006ab9"), 2000, 1000)
                .setSmallIcon(R.drawable.logo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            noti.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        noti.setContentIntent(pendingIntent);
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
// Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Nouveau message");
// Moves events into the expanded layout
        inboxStyle.addLine(username + " : " + message);
        inboxStyle.addLine("Sur le channel : " + channelId);
// Moves the expanded layout object into the notification object.
        noti.setStyle(inboxStyle);
        Notification notification = noti.build();
        notification.flags|= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

    }
}
