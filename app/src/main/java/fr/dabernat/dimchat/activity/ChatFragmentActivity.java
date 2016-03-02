package fr.dabernat.dimchat.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.fragment.ChannelListFragment;
import fr.dabernat.dimchat.fragment.FriendListFragment;
import fr.dabernat.dimchat.fragment.MessageFragment;
import fr.dabernat.dimchat.fragment.PrivateMessageFragment;
import fr.dabernat.dimchat.model.Channel;
import fr.dabernat.dimchat.model.CurrentUser;
import fr.dabernat.dimchat.model.User;

public class ChatFragmentActivity extends AppCompatActivity {

    private static final String TAG = "ChatFragmentActivity";

    private ChannelListFragment channelListFragment;
    private FriendListFragment friendListFragment;
    private MessageFragment messageFragment;
    private PrivateMessageFragment privateMessageFragment;
    private CurrentUser currentUser;

    public AdapterView.OnItemClickListener onLvChannelItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(messageFragment != null) {
                if (findViewById(R.id.fragment_container_side) != null) {
                    messageFragment.setChannel((Channel) channelListFragment.getChannelAdapter().getItem(position));
                    messageFragment.setCurrentUser(currentUser);
                } else {
                    Intent messagingIntent = new Intent(getApplicationContext(), MessagingActivity.class);
                    messagingIntent.putExtra("channel", (Channel) channelListFragment.getChannelAdapter().getItem(position));
                    messagingIntent.putExtra("currentUser", currentUser);
                    startActivity(messagingIntent);
                }
            }
        }
    };

    public AdapterView.OnItemClickListener onGvFriendItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(friendListFragment != null) {
                if (findViewById(R.id.fragment_container_side) != null) {
                    privateMessageFragment.setUserFriend((User) friendListFragment.getFriendsListAdapter().getItem(position));
                    privateMessageFragment.setCurrentUser(currentUser);
                } else {
                    Intent privateMessageIntent = new Intent(getApplicationContext(), PrivateMessagingActivity.class);
                    privateMessageIntent.putExtra("userFriend", (User) friendListFragment.getFriendsListAdapter().getItem(position));
                    privateMessageIntent.putExtra("currentUser", currentUser);
                    startActivity(privateMessageIntent);
                }
            }
        }
    };

    public View.OnClickListener onBtFriendClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            friendListFragment = new FriendListFragment();
            privateMessageFragment = new PrivateMessageFragment();

            if (findViewById(R.id.fragment_container_side) != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_main, privateMessageFragment)
                        .replace(R.id.fragment_container_side, friendListFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_main, friendListFragment)
                        .addToBackStack(null)
                        .commit();
//                Intent friendsIntent = new Intent(ChatFragmentActivity.this, FriendsActivity.class);
//                friendsIntent.putExtra("currentUser", currentUser);
//                startActivity(friendsIntent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        currentUser = (CurrentUser) getIntent().getSerializableExtra("currentUser");

        Log.w(TAG, "onCreate: " + currentUser );

        channelListFragment = ChannelListFragment.newInstance(currentUser);

        messageFragment = new MessageFragment();

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
