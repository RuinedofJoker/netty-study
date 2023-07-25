package org.joker.redis.client.task;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class ConsoleWriterListener implements Runnable{

    private Thread shutdownThread;
    private Channel channel;
    private InetSocketAddress redisServerAddress;

    public ConsoleWriterListener(Thread shutdownThread, Channel channel, InetSocketAddress redisServerAddress) {
        this.shutdownThread = shutdownThread;
        this.channel = channel;
        this.redisServerAddress = redisServerAddress;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.print(redisServerAddress.getHostString() + ":" + redisServerAddress.getPort() + ">");
                String input = scanner.nextLine();
                if ("exit".equals(input)) {
                    LockSupport.unpark(shutdownThread);
                    break;
                }
                channel.writeAndFlush(input).sync();
            } catch (Exception e) {
                log.error("{}", e);
            }
        }
    }
}
