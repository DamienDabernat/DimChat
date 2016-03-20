package fr.dabernat.dimchat.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link PrivateMessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrivateMessageFragment extends Fragment {

    private static final String TAG = "MessageFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String USER_FRIEND = "user_friend";
    public static final String CURRENT_USER = "current_user";

    private ListView lvMessage;
    private NewtonCradleLoading newtonCradleLoading;
    private RelativeLayout rlLoading;
    private RelativeLayout rlNoChannel;
    private User userFriend;
    private CurrentUser currentUser;
    private MessagingListAdapter messagingListAdapter;
    private Handler handler;
    private Runnable runnable;
    private Boolean firstLaunch = true;

    private View view;

    public PrivateMessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageFragment.
     */
    public static PrivateMessageFragment newInstance(User userFriend, CurrentUser currentUser) {
        PrivateMessageFragment fragment = new PrivateMessageFragment();
        Bundle args = new Bundle();
        args.putSerializable(PrivateMessageFragment.USER_FRIEND, userFriend);
        args.putSerializable(PrivateMessageFragment.CURRENT_USER, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userFriend = (User) getArguments().getSerializable(USER_FRIEND);
            currentUser = (CurrentUser) getArguments().getSerializable(CURRENT_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_message, container, false);

        Log.w(TAG, "onCreate: user " + currentUser);

        lvMessage = (ListView) view.findViewById(R.id.lvMessage);
        messagingListAdapter = new MessagingListAdapter(getContext());
        lvMessage.setAdapter(messagingListAdapter);

        rlLoading = (RelativeLayout) view.findViewById(R.id.rlLoading);
        newtonCradleLoading = (NewtonCradleLoading) view.findViewById(R.id.newton_cradle_loading);
        newtonCradleLoading.start();

        lvMessage.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        lvMessage.setStackFromBottom(true);

        lvMessage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Voulez-vous vraiment ajouter cet utilisateur à votre liste d'amis ?")
                        .setTitle("Ajouter un ami")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // ADD ZE FRIEND Lelz !
                                Message message = (Message) messagingListAdapter.getItem(position);
                                Log.w(TAG, "onClick: " + message.toString());
                                User user = new User(message.getUserID(), message.getUsername(), message.getDate(), message.getImageUrl());
                                UserTable.insert(user);
                                Toast.makeText(getActivity(), message.getUsername() + " ajouté(e) en ami(e)", Toast.LENGTH_SHORT).show();
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

        final EditText etMessage = (EditText) view.findViewById(R.id.etMessage);
        etMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvMessage.setSelection(messagingListAdapter.getCount() - 1);
            }
        });

        ImageButton btSend = (ImageButton) view.findViewById(R.id.btSend);
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

        hideSoftKeyboard();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                rlNoChannel = (RelativeLayout) view.findViewById(R.id.rlNoChannel);
                if(currentUser == null || userFriend == null) {
                    rlNoChannel.setVisibility(View.VISIBLE);
                } else {
                    rlNoChannel.setVisibility(View.INVISIBLE);
                    getMessages(currentUser, userFriend, messagingListAdapter);
                }
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 500);

    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
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

    public void setUserFriend(User userFriend) {
        this.userFriend = userFriend;
    }

    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getActivity().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}
