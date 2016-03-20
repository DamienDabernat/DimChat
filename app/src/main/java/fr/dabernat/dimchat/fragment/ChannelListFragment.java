package fr.dabernat.dimchat.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.victor.loading.newton.NewtonCradleLoading;

import java.util.HashMap;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.activity.ChatFragmentActivity;
import fr.dabernat.dimchat.activity.PrivateChatFragmentActivity;
import fr.dabernat.dimchat.adapter.ChannelListAdapter;
import fr.dabernat.dimchat.model.ChannelList;
import fr.dabernat.dimchat.model.CurrentUser;
import fr.dabernat.dimchat.server.OnServiceListener;
import fr.dabernat.dimchat.server.ServiceInterface;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ChannelListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChannelListFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = "ChannelListFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String CURRENT_USER = "current_user";

    private Button btFriends;
    private ListView lvChannel;
    private CurrentUser currentUser;
    private ChannelListAdapter channelAdapter;
    private NewtonCradleLoading newtonCradleLoading;
    private RelativeLayout rlLoading;

    public ChannelListFragment() {
        setHasOptionsMenu(true);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currentUser Parameter 1.
     * @return A new instance of fragment ChannelListFragment.
     */
    public static ChannelListFragment newInstance(CurrentUser currentUser) {
        ChannelListFragment fragment = new ChannelListFragment();
        Bundle args = new Bundle();
        args.putSerializable(CURRENT_USER, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_channel_list, container, false);

        btFriends = (Button) v.findViewById(R.id.btFriends);
        lvChannel = (ListView) v.findViewById(R.id.lvChannel);
        rlLoading = (RelativeLayout) v.findViewById(R.id.rlLoading);
        newtonCradleLoading = (NewtonCradleLoading) v.findViewById(R.id.newton_cradle_loading);
        newtonCradleLoading.start();

        getChannelList();

        lvChannel.setOnItemClickListener(((ChatFragmentActivity) getActivity()).onLvChannelItemClick);

        btFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privateChatFragmentActivity = new Intent(getContext(), PrivateChatFragmentActivity.class);
                privateChatFragmentActivity.putExtra("currentUser", currentUser);
                startActivity(privateChatFragmentActivity);
            }
        });

        return v;
    }

    public void getChannelList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accesstoken", currentUser.getToken());
        ServiceInterface serviceInterface = new ServiceInterface("POST", "messaging/", "getchannels", params);
        serviceInterface.setOnServiceListener(new OnServiceListener() {
            @Override
            public void onResult(String response) {
                Gson gson = new Gson();
                ChannelList channelList = gson.fromJson(response, ChannelList.class);
                channelAdapter = new ChannelListAdapter(getContext(), channelList.getChannels());
                lvChannel.setAdapter(channelAdapter);
                newtonCradleLoading.stop();
                rlLoading.setVisibility(View.INVISIBLE);
            }
        });

        serviceInterface.execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            currentUser = (CurrentUser) getArguments().getSerializable(CURRENT_USER);
            if(currentUser == null){
                currentUser = new CurrentUser();
                final SharedPreferences prefs = getActivity().getSharedPreferences(
                        "fr.dabernat.dimchat", Context.MODE_PRIVATE);
                currentUser.setPseudo(prefs.getString("username", ""));
                currentUser.setPassword(prefs.getString("password", ""));
                currentUser.setToken(prefs.getString("token", ""));
            }
            Log.w(TAG, "onCreate: " + currentUser.toString() + getArguments().toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public ChannelListAdapter getChannelAdapter() {
        return channelAdapter;
    }

    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        channelAdapter.setSearchResult(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //if(channelAdapter != null) {
            channelAdapter.setSearchResult(newText);
        //}
        return true;
    }
}
