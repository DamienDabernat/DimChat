package fr.dabernat.dimchat.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.fragment.MessageFragment;
import fr.dabernat.dimchat.model.Channel;
import fr.dabernat.dimchat.model.CurrentUser;

public class MessagingActivity extends GpsActivity {

    private static final String TAG = "MessagingActivity";
    
    private MessageFragment messageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        CurrentUser currentUser = (CurrentUser) getIntent().getSerializableExtra("currentUser");
        Channel channel = (Channel) getIntent().getSerializableExtra("channel");

        if(currentUser == null) {
            final SharedPreferences prefs = getSharedPreferences(
                    "fr.dabernat.dimchat", Context.MODE_PRIVATE);
            currentUser = new CurrentUser();
            currentUser.setPseudo(prefs.getString("username", ""));
            currentUser.setPassword(prefs.getString("password", ""));
            currentUser.setToken(prefs.getString("token", ""));
        }

        Log.w(TAG, "onCreate: " + currentUser + channel );
        
        messageFragment = MessageFragment.newInstance(channel, currentUser);
        messageFragment.setChannel(channel);
        messageFragment.setCurrentUser(currentUser);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_main, messageFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
