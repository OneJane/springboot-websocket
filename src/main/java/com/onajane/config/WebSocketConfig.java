package com.onajane.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocketMessageBroker // 启用websocket服务器
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 客户端使用 registerStompEndpoints 注册的websocket端点连接到websocket服务器
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 配置消息代理 将消息从一个客户端路由到另一个客户端
        // app路由到消息处理方法
        registry.setApplicationDestinationPrefixes("/app");

        // topic路由到消息代理，向订阅特定主题的所有链接客户端广播消息 enableSimpleBroker基于内存的消息代理
        registry.enableSimpleBroker("/topic");


        //   STOMP定义数据交换的格式和规则的消息传递协议，可以用于rabbitMQ活ActivateMQ
        /*
        registry.enableStompBrokerRelay("/topic")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");
        */
    }
}
