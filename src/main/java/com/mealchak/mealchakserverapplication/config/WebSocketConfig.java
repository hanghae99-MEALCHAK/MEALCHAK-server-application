package com.mealchak.mealchakserverapplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 해당 경로로 simplebroker를 등록한다 simplebroker는 해당하는 경로를 구독하는 client에게 메시지를 전달하는 간단한 작업을 수행한다
        config.enableSimpleBroker("/sub");
        // client에서 send요청을 처리한다
        config.setApplicationDestinationPrefixes("/pub");
        // For example, /app/hello is the endpoint that the GreetingController.greeting() method is mapped to handle.
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 핸드쉐이크와 통신 담당
        registry.addEndpoint("/ws-stomp").withSockJS();
    }


}