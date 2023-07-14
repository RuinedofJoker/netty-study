package org.example.chat.servlet;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import org.example.chat.constant.PathConstant;
import org.example.chat.pojo.ChatChannel;
import org.example.chat.pojo.User;
import org.example.chat.protocol.Hddp;
import org.example.chat.server.service.ChatChannelService;
import org.example.chat.server.service.ChatService;
import org.example.chat.server.service.UserService;
import org.example.chat.server.service.impl.UserServiceImpl;

import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public class DispatcherServlet extends ChannelInboundHandlerAdapter {

    private static UserService userService;
    private static ChatChannelService chatChannelService;
    private static ChatService chatService;

    private static final ConcurrentHashMap<SocketChannel, String> onlineMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ChatChannel> chatChannelMap = new ConcurrentHashMap<>();

    static {
        userService = new UserServiceImpl();
    }

    public static ConcurrentHashMap<SocketChannel, String> getOnlineMap() {
        return onlineMap;
    }
    public static ConcurrentHashMap<String, ChatChannel> getChatChannelMap() {
        return chatChannelMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Hddp hddp = (Hddp) msg;
        SocketChannel channel = (SocketChannel) ctx.channel();

        Hddp response = new Hddp();

        if (hddp.getPath().equals(PathConstant.OPERATE_CHANNEL)) {

        }else if (hddp.getPath().equals(PathConstant.OPERATE_CHAT)) {
            hddp.getContent();
        }else if (hddp.getPath().equals(PathConstant.LOGIN)) {
            User user = new Gson().fromJson(hddp.getContent(), User.class);
            if (userService.login(user)) {
                onlineMap.put(channel, user.getUsername());
                response.content("登录成功");
            }else {
                response.content("登录失败");
            }
        }else if (hddp.getPath().equals(PathConstant.LOGOUT)) {
            userService.logout(channel);
        }

        channel.writeAndFlush(response.build());
        super.channelRead(ctx, msg);
    }
}
