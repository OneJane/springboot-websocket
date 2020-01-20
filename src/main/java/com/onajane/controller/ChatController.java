package com.onajane.controller;

import com.onajane.model.ChatMessage;
import com.onajane.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Value("${redis.channel.msgToAll}")
    private String msgToAll;

    @Value("${redis.set.onlineUsers}")
    private String onlineUsers;

    @Value("${redis.channel.userStatus}")
    private String userStatus;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    // setApplicationDestinationPrefixes标识 以app开头的客户端发送的消息讲路由到@MessageMapping注释的消息处理方法
    // 例如将具有目标/app/chat.sendMessage的消息将路由到sendMessage，具有目标/app/chat.addUser的消息将路由到addUser
    // @SendTo("/topic/public")  前端发给我们消息后，直接给/topic/public转发这个消息，让其他用户收到
    // 集群中，我们需要把消息转发给Redis，并且不转发给前端，而是让服务端监听Redis消息，在进行消息发送
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        try {
            // JsonUtil将实体类ChatMessage转为了Json发送给了Redis
            redisTemplate.convertAndSend(msgToAll, JsonUtil.parseObjToJson(chatMessage));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

        logger.info("User added in Chatroom:" + chatMessage.getSender());
        try {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
            // 这里往redis中广播用户上线的消息，并把用户名username写入redis的set中（websocket.onlineUsers）
            redisTemplate.opsForSet().add(onlineUsers, chatMessage.getSender());
            redisTemplate.convertAndSend(userStatus, JsonUtil.parseObjToJson(chatMessage));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
