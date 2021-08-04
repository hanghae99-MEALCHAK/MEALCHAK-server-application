package com.mealchak.mealchakserverapplication.model;

import com.mealchak.mealchakserverapplication.dto.request.ChatMessageRequestDto;
import com.mealchak.mealchakserverapplication.service.UserService;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends Timestamped {

    public enum MessageType {
        ENTER, TALK, QUIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @Enumerated(value = EnumType.STRING)
    private MessageType type;

    @Column
    private String roomId;

    @Column
    private String message;

    @ManyToOne
    private User sender;

    @Builder
    public ChatMessage(MessageType type, String roomId, String message, User sender) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
    }

    @Builder
    public ChatMessage(ChatMessageRequestDto chatMessageRequestDto, UserService userService) {
        this.type = chatMessageRequestDto.getType();
        this.roomId = chatMessageRequestDto.getRoomId();
        this.sender =  userService.getUser(chatMessageRequestDto.getSenderId());
        this.message = chatMessageRequestDto.getMessage();
    }
}
