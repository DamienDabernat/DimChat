package fr.dabernat.dimchat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.fragment.PrivateMessageFragment;
import fr.dabernat.dimchat.model.CurrentUser;
import fr.dabernat.dimchat.model.User;

public class PrivateMessagingActivity extends ChatFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        CurrentUser currentUser = (CurrentUser) getIntent().getSerializableExtra("currentUser");
        User userFriend = (User) getIntent().getSerializableExtra("userFriend");

        PrivateMessageFragment privateMessageFragment = new PrivateMessageFragment();
        privateMessageFragment.setCurrentUser(currentUser);
        privateMessageFragment.setUserFriend(userFriend);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_main, privateMessageFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
