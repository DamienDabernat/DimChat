package fr.dabernat.dimchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.model.Channel;

public class ChannelListAdapter extends BaseAdapter {

    private Context context;
    private List<Channel> channelList;
    private List<Channel> originalChannelList;


    public ChannelListAdapter(Context context, List<Channel> channelList) {
        this.context = context;
        this.channelList = channelList;
        this.originalChannelList = channelList;
    }

    public void setSearchResult(String query) {
        List<Channel> resultChannelList = new ArrayList<>();
        for(Channel channel : originalChannelList) {
            if(channel.getName().toLowerCase().contains(query.toLowerCase())) {
                resultChannelList.add(channel);
            }
        }

        this.channelList = resultChannelList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return channelList.size();
    }

    @Override
    public Object getItem(int position) {
        return channelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_channel, parent, false);

        TextView name = (TextView) view.findViewById(R.id.tvName);
        TextView description = (TextView) view.findViewById(R.id.tvDescription);

        name.setText(channelList.get(position).getName());
        description.setText("Nombre de participant : " + channelList.get(position).getConnectedUsers());

        return view;
    }
}