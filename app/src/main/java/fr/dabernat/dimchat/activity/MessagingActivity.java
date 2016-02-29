package fr.dabernat.dimchat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;

import fr.dabernat.dimchat.adapter.MessagingListAdapter;
import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.database.DatabaseHelper;
import fr.dabernat.dimchat.database.table.UserTable;
import fr.dabernat.dimchat.model.Channel;
import fr.dabernat.dimchat.model.Message;
import fr.dabernat.dimchat.model.MessageList;
import fr.dabernat.dimchat.model.CurrentUser;
import fr.dabernat.dimchat.model.User;
import fr.dabernat.dimchat.server.OnServiceListener;
import fr.dabernat.dimchat.server.ServiceInterface;

public class MessagingActivity extends Activity {

    private static final String TAG = "MessagingActivity";
    private ListView lvMessage;
    private Channel channel;
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
        channel  = (Channel) getIntent().getSerializableExtra("channel");

        lvMessage = (ListView) findViewById(R.id.lvMessage);
        messagingListAdapter = new MessagingListAdapter(getApplicationContext());
        lvMessage.setAdapter(messagingListAdapter);

        lvMessage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MessagingActivity.this);
                builder.setMessage("Voulez-vous vraiment ajouter cet utilisateur à votre liste d'amis ?")
                        .setTitle("Ajouter un ami")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // ADD ZE FRIEND Lelz !
                                Message message = (Message) messagingListAdapter.getItem(position);
                                Log.w(TAG, "onClick: " + message.toString());
                                User user = new User(message.getUserID(), message.getUsername(), message.getDate(), message.getImageUrl());
                                UserTable.insert(user);
                                Toast.makeText(MessagingActivity.this, message.getUsername() + " ajouté(e) en ami(e)", Toast.LENGTH_SHORT).show();
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

        final EditText etMessage = (EditText) findViewById(R.id.etMessage);
        Button btSend = (Button) findViewById(R.id.btSend);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("accesstoken", currentUser.getToken());
                params.put("channelid", String.valueOf(channel.getChannelID()));
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

        hideSoftKeyboard();
    }

    private void getMessages(CurrentUser currentUser, Channel channel, final MessagingListAdapter messagingListAdapter) {
        HashMap<String, String> params = new HashMap<>();
        params.put("accesstoken", currentUser.getToken());
        params.put("channelid", String.valueOf(channel.getChannelID()));
        ServiceInterface serviceInterface = new ServiceInterface("POST", "messaging/", "getmessages", params);
        serviceInterface.setOnServiceListener(new OnServiceListener() {
            @Override
            public void onResult(String response) {
                if(!response.isEmpty()) {
                    Gson gson = new Gson();
                    MessageList messageList = gson.fromJson(response, MessageList.class);
                    messagingListAdapter.setMessageList(messageList.getMessageList());
                    if(firstLaunch) {
                        lvMessage.setSelection(messagingListAdapter.getCount() - 1);
                        firstLaunch = false;
                    }
                    if (lvMessage.getLastVisiblePosition() == lvMessage.getAdapter().getCount() - 1 &&
                            lvMessage.getChildAt(lvMessage.getChildCount() - 1).getBottom() <= lvMessage.getHeight()) {
                        lvMessage.setSelection(messagingListAdapter.getCount() - 1);
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
                getMessages(currentUser, channel, messagingListAdapter);
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

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
