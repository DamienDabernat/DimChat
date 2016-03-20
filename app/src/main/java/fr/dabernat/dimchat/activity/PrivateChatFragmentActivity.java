package fr.dabernat.dimchat.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

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

public class PrivateChatFragmentActivity extends GpsActivity {

    private static final String TAG = "ChatFragmentActivity";

    private FriendListFragment friendListFragment;
    private PrivateMessageFragment privateMessageFragment;
    private CurrentUser currentUser;

    public AdapterView.OnItemClickListener onGvFriendItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (friendListFragment != null) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        currentUser = (CurrentUser) getIntent().getSerializableExtra("currentUser");

        friendListFragment = new FriendListFragment();
        friendListFragment.setCurrentUser(currentUser);

        privateMessageFragment = new PrivateMessageFragment();

        if (findViewById(R.id.fragment_container_side) != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_main, privateMessageFragment)
                    .replace(R.id.fragment_container_side, friendListFragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_main, friendListFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
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
