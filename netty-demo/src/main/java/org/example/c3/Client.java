package org.example.c3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * io多路复用客户端
 */
public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();

        sc.connect(new InetSocketAddress("localhost", 8080));
        Scanner scanner = new Scanner(System.in);
        String content = "";

        while (true) {
            System.out.print("请输入:");
            content = scanner.nextLine();
            if ("exit".equals(content)) {
                break;
            }
            content += "\n";
            ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());
            sc.write(buffer);
        }
    }
}
