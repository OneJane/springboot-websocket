package com.onajane.controller;

import com.onajane.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    // setApplicationDestinationPrefixes标识 以app开头的客户端发送的消息讲路由到@MessageMapping注释的消息处理方法
    // 例如将具有目标/app/chat.sendMessage的消息将路由到sendMessage，具有目标/app/chat.addUser的消息将路由到addUser
    // @SendTo("/topic/public")  前端发给我们消息后，直接给/topic/public转发这个消息，让其他用户收到
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        logger.info("User added in Chatroom:" + chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

}
