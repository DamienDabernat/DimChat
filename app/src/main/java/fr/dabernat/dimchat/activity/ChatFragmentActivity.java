package fr.dabernat.dimchat.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.database.table.UserTable;
import fr.dabernat.dimchat.fragment.ChannelListFragment;
import fr.dabernat.dimchat.fragment.FriendListFragment;
import fr.dabernat.dimchat.fragment.MessageFragment;
import fr.dabernat.dimchat.fragment.PrivateMessageFragment;
import fr.dabernat.dimchat.model.Channel;
import fr.dabernat.dimchat.model.CurrentUser;
import fr.dabernat.dimchat.model.Message;
import fr.dabernat.dimchat.model.User;
import fr.dabernat.dimchat.server.UploadFileToServer;
import fr.dabernat.dimchat.utils.ImageConverter;

public class ChatFragmentActivity extends GpsActivity {

    private static final String TAG = "ChatFragmentActivity";

    protected ChannelListFragment channelListFragment;
    protected MessageFragment messageFragment;
    private CurrentUser currentUser;

    public AdapterView.OnItemClickListener onLvChannelItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (messageFragment != null) {
                if (findViewById(R.id.fragment_container_side) != null) {
                    messageFragment = MessageFragment.newInstance((Channel) channelListFragment.getChannelAdapter().getItem(position), currentUser);
                    messageFragment.setChannel((Channel) channelListFragment.getChannelAdapter().getItem(position));
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_main, messageFragment)
                            .commit();
                } else {
                    Intent messagingIntent = new Intent(ChatFragmentActivity.this, MessagingActivity.class);
                    messagingIntent.putExtra("channel", (Channel) channelListFragment.getChannelAdapter().getItem(position));
                    messagingIntent.putExtra("currentUser", currentUser);
                    startActivity(messagingIntent);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        currentUser = (CurrentUser) getIntent().getSerializableExtra("currentUser");

        Log.w(TAG, "onCreate: " + currentUser);

        messageFragment = new MessageFragment();
        channelListFragment = ChannelListFragment.newInstance(currentUser);

        if (findViewById(R.id.fragment_container_side) != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_main, messageFragment)
                        .replace(R.id.fragment_container_side, channelListFragment)
                        .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_main, channelListFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_channel, menu);
            MenuItem searchItem = menu.findItem(R.id.grid_default_search);
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(channelListFragment);

            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }

        if(item.getItemId()==R.id.show_friends) {
            Intent privateChatFragmentActivity = new Intent(ChatFragmentActivity.this, PrivateChatFragmentActivity.class);
            privateChatFragmentActivity.putExtra("currentUser", currentUser);
            startActivity(privateChatFragmentActivity);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
