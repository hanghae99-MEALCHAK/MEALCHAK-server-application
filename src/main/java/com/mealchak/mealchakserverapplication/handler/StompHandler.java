package com.mealchak.mealchakserverapplication.handler;

import com.mealchak.mealchakserverapplication.jwt.JwtTokenProvider;
import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import com.mealchak.mealchakserverapplication.service.AllChatInfoService;
import com.mealchak.mealchakserverapplication.service.ChatMessageService;
import com.mealchak.mealchakserverapplication.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatService;
    private final UserRepository userRepository;
    private final AllChatInfoService allChatInfoService;

    @Override
    // 클라이언트가 메세지를 발송하면 최초에 해당 메세지를 인터셉트하여 처리함
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String jwtToken = accessor.getFirstNativeHeader("token");
            log.info("CONNECT {}", jwtToken);
            jwtTokenProvider.validateToken(jwtToken);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청
            // header 정보에서 구독 destination 정보를 얻고, roomId를 추출한다.
            String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));

            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
            String sessionId = (String) message.getHeaders().get("simpSessionId");

            // 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
            //토큰 가져옴
            String jwtToken = accessor.getFirstNativeHeader("token");
            User user;
            if (jwtToken != null) {
                //토큰으로 user 가져옴
                user = userRepository.findByEmail(jwtTokenProvider.getUserPk(jwtToken), User.class)
                        .orElseThrow(()->new IllegalArgumentException("user 가 존재하지 않습니다."));
            }else {
                throw new IllegalArgumentException("유효하지 않은 token 입니다.");
            }
            Long userId = user.getId();
            chatRoomService.setUserEnterInfo(sessionId, roomId,userId);
            chatService.sendChatMessage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.ENTER)
                    .roomId(roomId)
                    .sender(user)
                    .build());
            log.info("SUBSCRIBED {}, {}", user.getUsername(), roomId);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
            // 연결이 종료된 클라이언트 sessionId 로 채팅방 id를 얻는다.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomService.getUserEnterRoomId(sessionId);
            // 저장했던 sessionId 로 유저 객체를 받아옴
            User user = chatRoomService.chkSessionUser(sessionId);
            String username = user.getUsername();
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
            log.info("DISCONNECTED {}, {}", username, roomId);
            // 유저가 퇴장할 당시의 마지막 TALK 타입 메세지 id 를 저장함
            allChatInfoService.updateReadMessage(user,roomId);
        }
        return message;
    }
}
