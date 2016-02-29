package fr.dabernat.dimchat.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.adapter.FriendsListAdapter;
import fr.dabernat.dimchat.database.table.UserTable;
import fr.dabernat.dimchat.model.CurrentUser;
import fr.dabernat.dimchat.model.Message;
import fr.dabernat.dimchat.model.User;

public class FriendsActivity extends AppCompatActivity {

    private static final String TAG = "FriendsActivity";

    private CurrentUser currentUser;
    private GridView glFriends;
    private FriendsListAdapter friendsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        currentUser = (CurrentUser) getIntent().getSerializableExtra("currentUser");

        glFriends = (GridView) findViewById(R.id.gvFriends);
        TextView tvNoFriends = (TextView) findViewById(R.id.tvNoFriends);

        List<User> userList = UserTable.getAll();

        if(userList.isEmpty()) {
            glFriends.setVisibility(View.INVISIBLE);
            tvNoFriends.setVisibility(View.VISIBLE);
            tvNoFriends.setText("Vous n'avez pas encore ajouté d'amis :(");
        } else {
            friendsListAdapter = new FriendsListAdapter(FriendsActivity.this, currentUser);
            friendsListAdapter.setUserList(userList);
            glFriends.setAdapter(friendsListAdapter);
            friendsListAdapter.notifyDataSetChanged();

            glFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    Intent privateMessageIntent = new Intent(FriendsActivity.this, PrivateMessagingActivity.class);
                    User user = (User) friendsListAdapter.getItem(position);
                    privateMessageIntent.putExtra("userFriend", user);
                    privateMessageIntent.putExtra("currentUser", currentUser);
                    startActivity(privateMessageIntent);
                }
            });

            glFriends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                    builder.setMessage("Voulez-vous vraiment supprimé cet utilisateur à votre liste d'amis ?")
                            .setTitle("Supprimé un ami")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // DEL ZE FRIEND Lelz !
                                    User user = (User) friendsListAdapter.getItem(position);
                                    UserTable.remove(user.getUserID());
                                    Toast.makeText(FriendsActivity.this, user.getPseudo() + " supprimé", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    //Do NOTHING LEL
                                }
                            });
                    builder.show();
                    return true;
                }
            });
        }
    }
}
