package org.example.chat.pojo;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class ChatChannel {
    String channelName;
    String orderName;
    ConcurrentHashMap<String, SocketChannel> member;

    public ChatChannel(String channelName, String orderName) {
        this.channelName = channelName;
        this.orderName = orderName;
        this.member = new ConcurrentHashMap<>();
    }

    public void joinMember(String username, SocketChannel userChannel) {
        member.put(username, userChannel);
    }

    public void leaveChannel(String username) {
        SocketChannel usernameChanel = member.remove(username);
    }

    public void leaveChannel(SocketChannel usernameChanel) {


    }
}
