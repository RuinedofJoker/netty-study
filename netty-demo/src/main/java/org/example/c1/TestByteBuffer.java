package org.example.c1;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TestByteBuffer {
    public static void main(String[] args) {
        int len = 0;
        List<Byte> list = new ArrayList<>();
        try (
                FileChannel channel = new FileInputStream(TestByteBuffer.class.getResource("/test.txt").getPath()).getChannel();
        ) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            while (len != -1) {
                len = channel.read(byteBuffer);
                //flip()切换为读模式
                byteBuffer.flip();

                while (byteBuffer.hasRemaining()) {
                    list.add(byteBuffer.get());
                }

                //clear()或compact()切换为写模式
                byteBuffer.compact();
            }

        }catch (Exception e) {

        }
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        System.out.println(new String(bytes));

        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode("hello");
        String hello = StandardCharsets.UTF_8.decode(byteBuffer).toString();
    }
}
