package com.mealchak.mealchakserverapplication.handler;

import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.jwt.JwtTokenProvider;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsServiceImpl;
import com.mealchak.mealchakserverapplication.service.ChatRoomService;
import com.mealchak.mealchakserverapplication.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String jwtToken = accessor.getFirstNativeHeader("token");
            log.info("CONNECT {}", jwtToken);
            jwtTokenProvider.validateToken(jwtToken);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {// 채팅룸 구독요청
            // header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
            String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            chatRoomService.setUserEnterInfo(sessionId, roomId);
            // 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
            //토큰 가져옴
            String jwtToken = accessor.getFirstNativeHeader("token");
            String name;
            if (jwtToken != null) {
                //토큰으로 userDetails 가져옴
                UserDetails userDetails = userDetailsService.loadUserByUsername(jwtTokenProvider.getUserPk(jwtToken));
                //userDetails 에서 username 가져옴
                name = userDetails.getUsername();
            } else {
                name = "UnknownUser";
            }
            chatService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.ENTER).roomId(roomId).sender(name).build());
            log.info("SUBSCRIBED {}, {}", name, roomId);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
            // 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomService.getUserEnterRoomId(sessionId);
            // 클라이언트 퇴장 메시지를 채팅방에 발송한다.(redis publish)
            //토큰 가져옴
//            String jwtToken = accessor.getFirstNativeHeader("token");
//            //토큰으로 userDetails 가져옴
//            UserDetails userDetails = userDetailsService.loadUserByUsername(jwtTokenProvider.getUserPk(jwtToken));
//            //userDetails 에서 username 가져옴
//            String name = userDetails.getUsername();
//            chatService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.QUIT).roomId(roomId).sender(name).build());
            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            chatRoomService.removeUserEnterInfo(sessionId);
            log.info("DISCONNECTED {}, {}", sessionId, roomId);
        }
        return message;
    }
}
