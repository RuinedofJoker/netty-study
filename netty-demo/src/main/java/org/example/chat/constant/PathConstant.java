package org.example.chat.constant;

public class PathConstant {
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String OPERATE_CHANNEL = "/channel";
    public static String channel(String channelName) {
        return OPERATE_CHANNEL + channelName;
    }

    public static final String OPERATE_CHAT = "/chat";
    public static String chat(String channelName) {
        return OPERATE_CHAT + channelName;
    }
}
