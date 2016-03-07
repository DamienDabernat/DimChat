package fr.dabernat.dimchat.activity;

import android.os.Bundle;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.fragment.MessageFragment;
import fr.dabernat.dimchat.model.Channel;
import fr.dabernat.dimchat.model.CurrentUser;

public class MessagingActivity extends ChatFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        CurrentUser currentUser = (CurrentUser) getIntent().getSerializableExtra("currentUser");
        Channel channel = (Channel) getIntent().getSerializableExtra("channel");

        messageFragment = new MessageFragment();
        messageFragment.setChannel(channel);
        messageFragment.setCurrentUser(currentUser);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_main, messageFragment)
                .commit();
    }
}
