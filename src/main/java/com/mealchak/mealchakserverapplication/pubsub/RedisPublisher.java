package com.mealchak.mealchakserverapplication.pubsub;

import com.mealchak.mealchakserverapplication.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisPublisher {
    // 메세지를 redis서버로 발행하는  publisher 추가, redis template를 주입받아 발행을 위한 메서드 구현
    private final RedisTemplate<String, Object> redisTemplate;

    // 토픽에 구독자가 있는 경우 발행자가 메세지를 발행하면 구독자에게 메세지가 전달됩니다.
    public void publish(ChannelTopic topic, ChatMessage message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}