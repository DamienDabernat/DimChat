package fr.dabernat.dimchat.activity;

import android.os.Bundle;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.fragment.FriendListFragment;
import fr.dabernat.dimchat.model.CurrentUser;

public class FriendsActivity extends ChatFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        CurrentUser currentUser = (CurrentUser) getIntent().getSerializableExtra("currentUser");

        FriendListFragment friendListFragment = new FriendListFragment();
        friendListFragment.setCurrentUser(currentUser);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_main, friendListFragment)
                .commit();

    }
}
