package fr.dabernat.dimchat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.victor.loading.newton.NewtonCradleLoading;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.adapter.MessagingListAdapter;
import fr.dabernat.dimchat.database.table.UserTable;
import fr.dabernat.dimchat.model.Channel;
import fr.dabernat.dimchat.model.CurrentUser;
import fr.dabernat.dimchat.model.Message;
import fr.dabernat.dimchat.model.MessageList;
import fr.dabernat.dimchat.model.User;
import fr.dabernat.dimchat.server.OnServiceListener;
import fr.dabernat.dimchat.server.ServiceInterface;

public class PrivateMessagingActivity extends AppCompatActivity {

    private static final String TAG = "MessagingActivity";
    private ListView lvMessage;
    private NewtonCradleLoading newtonCradleLoading;
    private RelativeLayout rlLoading;
    private User userFriend;
    private CurrentUser currentUser;
    private MessagingListAdapter messagingListAdapter;
    private Handler handler;
    private Runnable runnable;
    private Boolean firstLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        currentUser = (CurrentUser) getIntent().getSerializableExtra("currentUser");
        userFriend = (User) getIntent().getSerializableExtra("userFriend");

        rlLoading = (RelativeLayout) findViewById(R.id.rlLoading);
        newtonCradleLoading = (NewtonCradleLoading) findViewById(R.id.newton_cradle_loading);
        newtonCradleLoading.start();

        lvMessage = (ListView) findViewById(R.id.lvMessage);
        messagingListAdapter = new MessagingListAdapter(getApplicationContext());
        lvMessage.setAdapter(messagingListAdapter);

        final EditText etMessage = (EditText) findViewById(R.id.etMessage);

        etMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvMessage.setSelection(messagingListAdapter.getCount() - 1);
            }
        });

        ImageButton btSend = (ImageButton) findViewById(R.id.btSend);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("accesstoken", currentUser.getToken());
                params.put("userid", String.valueOf(userFriend.getUserID()));
                params.put("message", etMessage.getText().toString());

                ServiceInterface serviceInterface = new ServiceInterface("POST", "messaging/", "sendmessage", params);
                serviceInterface.setOnServiceListener(new OnServiceListener() {
                    @Override
                    public void onResult(String response) {
                        etMessage.setText("");
                    }
                });

                serviceInterface.execute();
            }
        });
    }

    private void getMessages(CurrentUser currentUser, User userFriend, final MessagingListAdapter messagingListAdapter) {
        HashMap<String, String> params = new HashMap<>();
        params.put("accesstoken", currentUser.getToken());
        params.put("userid", String.valueOf(userFriend.getUserID()));
        ServiceInterface serviceInterface = new ServiceInterface("POST", "messaging/", "getmessages", params);
        serviceInterface.setOnServiceListener(new OnServiceListener() {
            @Override
            public void onResult(String response) {
                if(!response.isEmpty()) {
                    newtonCradleLoading.stop();
                    rlLoading.setVisibility(View.INVISIBLE);
                    Gson gson = new Gson();
                    MessageList messageList = gson.fromJson(response, MessageList.class);
                    List<Message> messages = messageList.getMessageList();
                    Collections.reverse(messages);
                    messagingListAdapter.setMessageList(messages);
                    messagingListAdapter.notifyDataSetChanged();
                    if(firstLaunch) {
                        lvMessage.setSelection(messagingListAdapter.getCount() - 1);
                        firstLaunch = false;
                    }
                    if(lvMessage.getChildCount() > 1) {
                        if (lvMessage.getLastVisiblePosition() == lvMessage.getAdapter().getCount() - 1 &&
                                lvMessage.getChildAt(lvMessage.getChildCount() - 1).getBottom() <= lvMessage.getHeight()) {
                            lvMessage.setSelection(messagingListAdapter.getCount() - 1);
                        }
                    }
                }
            }
        });
        serviceInterface.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                getMessages(currentUser, userFriend, messagingListAdapter);
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 500);

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}
