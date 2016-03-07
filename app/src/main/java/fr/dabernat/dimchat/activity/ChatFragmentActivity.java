package fr.dabernat.dimchat.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class ChatFragmentActivity extends GpsActivity {

    private static final String TAG = "ChatFragmentActivity";

    protected ChannelListFragment channelListFragment;
    protected FriendListFragment friendListFragment;
    protected MessageFragment messageFragment;

    public AdapterView.OnItemLongClickListener onItemLongClickMessageListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

            String[] listOfOtpions = {"Ajouter en amis", "Voir sur la carte"};

            AlertDialog.Builder builder = new AlertDialog.Builder(ChatFragmentActivity.this);
            builder.setTitle("Ajouter un ami")
                    .setItems(listOfOtpions, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Message message = messageFragment.messagingListAdapter.getList().get(position);
                            User user = new User(message.getUserID(), message.getUsername(), message.getDate(), message.getImageUrl());

                            switch (which) {
                                case 0:
                                    // ADD ZE FRIEND Lelz !
                                    UserTable.insert(user);
                                    Toast.makeText(ChatFragmentActivity.this, message.getUsername() + " ajouté(e) en ami(e)", Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    Intent mapsIntent = new Intent(ChatFragmentActivity.this, MapsActivity.class);
                                    mapsIntent.putExtra("latitude", user.getLatitude());
                                    mapsIntent.putExtra("longitude", user.getLongitude());
                                    mapsIntent.putExtra("title", user.getPseudo());
                                    startActivity(mapsIntent);
                                    break;
                                default:
                                    Intent gpsIntent = new Intent(ChatFragmentActivity.this, GpsActivity.class);
                                    startActivity(gpsIntent);
                                    break;
                            }
                        }
                    });
            builder.show();
            return true;
        }
    };
    private PrivateMessageFragment privateMessageFragment;
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
    private CurrentUser currentUser;
    public AdapterView.OnItemClickListener onLvChannelItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (messageFragment != null) {
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

        Log.w(TAG, "onCreate: " + currentUser);

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
