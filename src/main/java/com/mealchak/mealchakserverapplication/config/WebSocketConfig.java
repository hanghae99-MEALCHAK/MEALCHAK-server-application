package com.mealchak.mealchakserverapplication.config;

import com.mealchak.mealchakserverapplication.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //구독용(sub)
        registry.enableSimpleBroker("/sub");
        //발행용(pub)
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //나중에 allowed origin 수정예정
        //웹소켓 연결 url
        registry.addEndpoint("/chatting").setAllowedOriginPatterns("*").withSockJS().setHeartbeatTime(1000);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);

    }
}