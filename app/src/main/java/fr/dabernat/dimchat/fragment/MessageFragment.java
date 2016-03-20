package fr.dabernat.dimchat.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.activity.ChatFragmentActivity;
import fr.dabernat.dimchat.activity.MapsActivity;
import fr.dabernat.dimchat.activity.MessagingActivity;
import fr.dabernat.dimchat.adapter.MessagingListAdapter;
import fr.dabernat.dimchat.database.table.UserTable;
import fr.dabernat.dimchat.model.Channel;
import fr.dabernat.dimchat.model.CurrentUser;
import fr.dabernat.dimchat.model.Message;
import fr.dabernat.dimchat.model.MessageList;
import fr.dabernat.dimchat.model.User;
import fr.dabernat.dimchat.server.OnServiceListener;
import fr.dabernat.dimchat.server.ServiceInterface;
import fr.dabernat.dimchat.server.UploadFileToServer;
import fr.dabernat.dimchat.utils.ImageConverter;


public class MessageFragment extends Fragment {

    public static final int PICTURE_REQUEST_CODE = 60;
    public static final String CHANNEL = "channel";
    public static final String CURRENT_USER = "current_user";
    private static final String TAG = "MessageFragment";
    public MessagingListAdapter messagingListAdapter;
    private ListView lvMessage;
    private NewtonCradleLoading newtonCradleLoading;
    private RelativeLayout rlLoading;
    private RelativeLayout rlNoChannel;
    public Channel channel;
    private CurrentUser currentUser;
    private Boolean firstLaunch = true;
    private ImageButton btPhoto;
    private Uri uri;
    private String mCurrentPhotoPath;
    private BroadcastReceiver mMessageReceiver;
    private MediaRecorder mRecorder = null;
    private static String mFileName = null;

    private Boolean start = true;

    private View view;
    private File photoFile;

    public MessageFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageFragment.
     */
    public static MessageFragment newInstance(Channel channel, CurrentUser currentUser) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putSerializable(MessageFragment.CHANNEL, channel);
        args.putSerializable(MessageFragment.CURRENT_USER, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            channel = (Channel) getArguments().getSerializable(CHANNEL);
            currentUser = (CurrentUser) getArguments().getSerializable(CURRENT_USER);
        }

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecord.3gp";

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                abortBroadcast() ;
                Log.w(TAG, "onReceive: " + intent.getExtras().keySet().toString());
                getMessages(currentUser, channel, messagingListAdapter);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_message, container, false);

        Log.w(TAG, "onCreate: user " + currentUser);

        lvMessage = (ListView) view.findViewById(R.id.lvMessage);
        messagingListAdapter = new MessagingListAdapter(getContext());
        lvMessage.setAdapter(messagingListAdapter);

        final ImageButton ibMic = (ImageButton) view.findViewById(R.id.ibMic);
        ibMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intentMic = new Intent(getContext(), AudioRecordTest.class);
                //getActivity().startActivityForResult(intentMic, AUDIO_REQUEST_CODE);
                if (start) {
                    startRecording();
                    ibMic.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_dark));
                } else {
                    stopRecording();
                    ibMic.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_mic_white_48dp));
                }
            }
        });

        if(currentUser != null) {
            getMessages(currentUser, channel, messagingListAdapter);
        }

        rlLoading = (RelativeLayout) view.findViewById(R.id.rlLoading);
        newtonCradleLoading = (NewtonCradleLoading) view.findViewById(R.id.newton_cradle_loading);
        newtonCradleLoading.start();

        lvMessage.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        lvMessage.setStackFromBottom(true);

        lvMessage.setOnItemLongClickListener(onItemLongClickMessageListener);

        btPhoto = (ImageButton) view.findViewById(R.id.btPhoto);

        btPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
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
                params.put("channelid", String.valueOf(channel.getChannelID()));
                params.put("message", etMessage.getText().toString());

                Activity activity;
                if(getActivity() instanceof ChatFragmentActivity) {
                    params.put("latitude", String.valueOf(((ChatFragmentActivity) getActivity()).mLatitude));
                    params.put("longitude", String.valueOf(((ChatFragmentActivity) getActivity()).mLongitude));
                } else if (getActivity() instanceof MessagingActivity) {
                    params.put("latitude", String.valueOf(((MessagingActivity) getActivity()).mLatitude));
                    params.put("longitude", String.valueOf(((MessagingActivity) getActivity()).mLongitude));
                }

                ServiceInterface serviceInterface = new ServiceInterface("POST", "messaging/", "sendmessage", params);
                serviceInterface.setOnServiceListener(new OnServiceListener() {
                    @Override
                    public void onResult(String response) {
                        etMessage.setText("");
                        getMessages(currentUser,channel, messagingListAdapter);
                        messagingListAdapter.notifyDataSetChanged();
                    }
                });

                serviceInterface.execute();
            }
        });

        hideSoftKeyboard();

        return view;
    }

    public AdapterView.OnItemLongClickListener onItemLongClickMessageListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

            String[] listOfOtpions = {"Ajouter en amis", "Voir sur la carte"};

            AlertDialog.Builder builder = new AlertDialog.Builder(MessageFragment.this.getActivity());
            builder.setTitle("Ajouter un ami")
                    .setItems(listOfOtpions, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Message message = messagingListAdapter.getList().get(position);
                            User user = new User(message.getUserID(), message.getUsername(), message.getDate(), message.getImageUrl());

                            switch (which) {
                                case 0:
                                    // ADD ZE FRIEND Lelz !
                                    UserTable.insert(user);
                                    Toast.makeText(MessageFragment.this.getActivity(), message.getUsername() + " ajouté(e) en ami(e)", Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    Intent mapsIntent = new Intent(MessageFragment.this.getActivity(), MapsActivity.class);
                                    mapsIntent.putExtra("latitude", message.getLatitude());
                                    mapsIntent.putExtra("longitude", message.getLongitude());
                                    mapsIntent.putExtra("title", user.getPseudo());
                                    startActivity(mapsIntent);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
            builder.show();
            return true;
        }
    };

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

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.google.android.c2dm.intent.RECEIVE");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        getContext().registerReceiver(mMessageReceiver, filter);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(mMessageReceiver);
    }

    private void getMessages(CurrentUser currentUser, Channel channel, final MessagingListAdapter messagingListAdapter) {
        HashMap<String, String> params = new HashMap<>();
        params.put("accesstoken", currentUser.getToken());
        params.put("channelid", String.valueOf(channel.getChannelID()));
        ServiceInterface serviceInterface = new ServiceInterface("POST", "messaging/", "getmessages", params);
        serviceInterface.setOnServiceListener(new OnServiceListener() {
            @Override
            public void onResult(String response) {
                rlLoading.setVisibility(View.INVISIBLE);
                Log.w(TAG, "onResult: " + response );
                if (!response.isEmpty()) {
                    newtonCradleLoading.stop();
                    rlLoading.setVisibility(View.INVISIBLE);
                    Gson gson = new Gson();
                    MessageList messageList = new MessageList();
                    messageList = gson.fromJson(response, MessageList.class);
                    List<Message> messages = messageList.getMessageList();
                    Collections.reverse(messages);
                    //List<Message> newMessageList = MessageHelper.getNewMessage(messagingListAdapter.getList(), messages);
                    //messagingListAdapter.addList(newMessageList);
                    messagingListAdapter.setMessageList(messages);
                    messagingListAdapter.notifyDataSetChanged();
                    if (firstLaunch) {
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

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == PICTURE_REQUEST_CODE) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("accesstoken", currentUser.getToken()));
            params.add(new BasicNameValuePair("channelid", channel.getChannelID() + ""));

            UploadFileToServer.OnUploadFileListener onUploadFileListener = new UploadFileToServer.OnUploadFileListener() {
                @Override
                public void onResponse(String result) {
                    Log.w(TAG, "onResponse: " + result);
                    getMessages(currentUser,channel, messagingListAdapter);
                    messagingListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailed(IOException error) {
                    Log.w(TAG, "onResponse: " + error.getMessage());
                }
            };

            try {
                ImageConverter.resizeFile(photoFile, getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }

            UploadFileToServer uploadFileToServer = new UploadFileToServer(getActivity(), photoFile.getPath(), params, onUploadFileListener);
            uploadFileToServer.execute();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, PICTURE_REQUEST_CODE);
            }
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
        start = false;
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("accesstoken", currentUser.getToken()));
        params.add(new BasicNameValuePair("channelid", channel.getChannelID() + ""));

        UploadFileToServer.OnUploadFileListener onUploadFileListener = new UploadFileToServer.OnUploadFileListener() {
            @Override
            public void onResponse(String result) {
                Log.w(TAG, "onResponse: " + result);
                getMessages(currentUser,channel, messagingListAdapter);
                messagingListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(IOException error) {
                Log.w(TAG, "onResponse: " + error.getMessage());
            }
        };

        UploadFileToServer uploadFileToServer = new UploadFileToServer(getActivity(), mFileName, params, onUploadFileListener);
        uploadFileToServer.execute();
    }

}
