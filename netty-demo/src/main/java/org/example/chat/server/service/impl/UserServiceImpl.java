package org.example.chat.server.service.impl;

import io.netty.channel.socket.SocketChannel;
import org.example.chat.pojo.User;
import org.example.chat.server.service.UserService;
import org.example.chat.servlet.DispatcherServlet;

import java.util.HashMap;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private static final Map<String, String> USER_DATA_BASE = new HashMap<>();

    static {
        USER_DATA_BASE.put("zhangsan", "123");
        USER_DATA_BASE.put("lisi", "123");
        USER_DATA_BASE.put("wangwu", "123");
    }

    @Override
    public boolean login(User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return false;
        }
        if (!USER_DATA_BASE.containsKey(user.getUsername())) {
            return false;
        }
        if (!USER_DATA_BASE.get(user.getUsername()).equals(user.getPassword())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean logout(SocketChannel socketChannel) {
        if (DispatcherServlet.getOnlineMap().contains(socketChannel)) {
            DispatcherServlet.getOnlineMap().remove(socketChannel);
            return true;
        }
        return false;
    }
}
