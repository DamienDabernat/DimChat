package fr.dabernat.dimchat.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.activity.ChatFragmentActivity;
import fr.dabernat.dimchat.activity.PrivateMessagingActivity;
import fr.dabernat.dimchat.adapter.FriendsListAdapter;
import fr.dabernat.dimchat.database.table.UserTable;
import fr.dabernat.dimchat.model.CurrentUser;
import fr.dabernat.dimchat.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link FriendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private CurrentUser currentUser;
    private GridView glFriends;
    private FriendsListAdapter friendsListAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendListFragment newInstance(String param1, String param2) {
        FriendListFragment fragment = new FriendListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        glFriends = (GridView) view.findViewById(R.id.gvFriends);
        TextView tvNoFriends = (TextView) view.findViewById(R.id.tvNoFriends);

        List<User> userList = UserTable.getAll();

        if(userList.isEmpty()) {
            glFriends.setVisibility(View.INVISIBLE);
            tvNoFriends.setVisibility(View.VISIBLE);
            tvNoFriends.setText("Vous n'avez pas encore ajouté d'amis :(");
        } else {
            friendsListAdapter = new FriendsListAdapter(getActivity(), currentUser);
            friendsListAdapter.setUserList(userList);
            glFriends.setAdapter(friendsListAdapter);
            friendsListAdapter.notifyDataSetChanged();

            glFriends.setOnItemClickListener(((ChatFragmentActivity) getActivity()).onGvFriendItemClick);

//            glFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                    Intent privateMessageIntent = new Intent(getActivity(), PrivateMessagingActivity.class);
//                    User user = (User) friendsListAdapter.getItem(position);
//                    privateMessageIntent.putExtra("userFriend", user);
//                    privateMessageIntent.putExtra("currentUser", currentUser);
//                    startActivity(privateMessageIntent);
//                }
//            });

            glFriends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Voulez-vous vraiment supprimé cet utilisateur à votre liste d'amis ?")
                            .setTitle("Supprimé un ami")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // DEL ZE FRIEND Lelz !
                                    User user = (User) friendsListAdapter.getItem(position);
                                    UserTable.remove(user.getUserID());
                                    Toast.makeText(getActivity(), user.getPseudo() + " supprimé", Toast.LENGTH_SHORT).show();
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
        }

        return view;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    public FriendsListAdapter getFriendsListAdapter() {
        return friendsListAdapter;
    }
}
