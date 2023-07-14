package org.example.chat.pojo;

import lombok.Data;

@Data
public class ChatMessage {
    private String channelName;
    private String sender;
    private String content;
}
