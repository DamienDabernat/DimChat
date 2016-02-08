package fr.dabernat.dimchat.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.HashMap;

import fr.dabernat.dimchat.adapter.ChannelListAdapter;
import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.model.Channel;
import fr.dabernat.dimchat.model.ChannelList;
import fr.dabernat.dimchat.model.CurrentUser;
import fr.dabernat.dimchat.server.OnServiceListener;
import fr.dabernat.dimchat.server.ServiceInterface;

public class ChannelListActivity extends AppCompatActivity {

    private ListView lvChannel;
    private ChannelListAdapter channelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);
        final CurrentUser currentUser = (CurrentUser) getIntent().getSerializableExtra("currentUser");

        lvChannel = (ListView) findViewById(R.id.lvChannel);

        HashMap<String, String> params = new HashMap<>();
        params.put("accesstoken", currentUser.getToken());
        ServiceInterface serviceInterface = new ServiceInterface("POST", "messaging/", "getchannels", params);
        serviceInterface.setOnServiceListener(new OnServiceListener() {
            @Override
            public void onResult(String response) {
                Gson gson = new Gson();
                ChannelList channelList = gson.fromJson(response, ChannelList.class);
                channelAdapter = new ChannelListAdapter(getApplicationContext(), channelList.getChannels());
                lvChannel.setAdapter(channelAdapter);
            }
        });

        serviceInterface.execute();

        lvChannel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent messagingIntent = new Intent(getApplicationContext(), MessagingActivity.class);
                messagingIntent.putExtra("channel", (Channel) channelAdapter.getItem(position));
                messagingIntent.putExtra("currentUser", currentUser);
                startActivity(messagingIntent);

            }
        });
    }
}
