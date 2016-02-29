package fr.dabernat.dimchat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.model.CurrentUser;

public class FriendsActivity extends AppCompatActivity {

    private CurrentUser currentUser;
    private ListView lvFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        currentUser = (CurrentUser) getIntent().getSerializableExtra("currentUser");

        lvFriends = (ListView) findViewById(R.id.lvFriends);
        TextView tvNoFriends = (TextView) findViewById(R.id.tvNoFriends);

        lvFriends.setVisibility(View.INVISIBLE);
        tvNoFriends.setVisibility(View.VISIBLE);
        tvNoFriends.setText("Vous n'avez pas encore ajouté d'amis :(");
    }
}
