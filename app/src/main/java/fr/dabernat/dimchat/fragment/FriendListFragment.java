package fr.dabernat.dimchat.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import fr.dabernat.dimchat.adapter.FriendsListAdapter;
import fr.dabernat.dimchat.database.table.UserTable;
import fr.dabernat.dimchat.model.CurrentUser;
import fr.dabernat.dimchat.model.User;

public class FriendListFragment extends Fragment {

    private CurrentUser currentUser;
    private FriendsListAdapter friendsListAdapter;


    public FriendListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        GridView glFriends = (GridView) view.findViewById(R.id.gvFriends);
        TextView tvNoFriends = (TextView) view.findViewById(R.id.tvNoFriends);

        List<User> userList = UserTable.getAll();

        if(userList.isEmpty()) {
            glFriends.setVisibility(View.INVISIBLE);
            tvNoFriends.setVisibility(View.VISIBLE);
            tvNoFriends.setText(R.string.no_friends);
        } else {
            friendsListAdapter = new FriendsListAdapter(getActivity(), currentUser);
            friendsListAdapter.setUserList(userList);
            glFriends.setAdapter(friendsListAdapter);
            friendsListAdapter.notifyDataSetChanged();

            glFriends.setOnItemClickListener(((ChatFragmentActivity) getActivity()).onGvFriendItemClick);

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
