package com.onajane.service;

import com.onajane.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    // 单机中 接收消息是通过Controller直接把消息转发到所有人的频道上，这样就能在所有人的聊天框显示
    // 集群中，我们需要服务器把消息从Redis中拿出来，并且推送到自己管的用户那边，我们在Service层实现消息的推送
    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    /**
     * 发送消息1：处理消息后发送消息
     *  使用 @MessageMapping 或者 @SubscribeMapping 注解可以处理客户端发送过来的消息，并选择方法是否有返回值。
     *  如果 @MessageMapping注解的控制器方法有返回值的话，返回值会被发送到消息代理，只不过会添加上"/topic"前缀。可以使用@SendTo 重写消息目的地；
     *  如果 @SubscribeMapping注解的控制器方法有返回值的话，返回值会直接发送到客户端，不经过代理。如果加上@SendTo 注解的话，则要经过消息代理
     * 应用任意地方发送消息：
     *  SimpMessageSendingOperations 接口（或者使用SimpMessagingTemplate ），可以实现自由的向任意目的地发送消息，并且订阅此目的地的所有用户都能收到消息
     * @param chatMessage
     */
    public void sendMsg(@Payload ChatMessage chatMessage) {
        LOGGER.info("Send msg by simpMessageSendingOperations:" + chatMessage.toString());
        simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
    }

    // 在service中我们向本服务器的用户广播消息，用户上线或者下线的消息都通过这里传达
    public void alertUserStatus(@Payload ChatMessage chatMessage) {
        LOGGER.info("Alert user online by simpMessageSendingOperations:" + chatMessage.toString());
        simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
    }
}
