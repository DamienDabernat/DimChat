package fr.dabernat.dimchat.model;

import java.util.List;

public class ChannelList {

    private List<Channel> channels;

    public ChannelList(List<Channel> channelList) {
        this.channels = channelList;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public String toString() {
        return "ChannelList{" +
                "channels=" + channels +
                '}';
    }
}
