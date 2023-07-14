package org.example.chat.server.service;

import io.netty.channel.socket.SocketChannel;
import org.example.chat.pojo.User;


public interface UserService {
    boolean login(User user);
    boolean logout(SocketChannel socketChannel);
}
