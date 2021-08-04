package com.mealchak.mealchakserverapplication.model;

import com.mealchak.mealchakserverapplication.dto.request.ChatMessageRequestDto;
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
    private String sender;

    @Column
    private String senderId;

    @Column
    private String senderImg;

    @Column
    private String message;


    @Builder
    public ChatMessage(MessageType type, String roomId, String sender, String senderId, String message) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.senderId = senderId;
        this.message = message;
    }

    @Builder
    public ChatMessage(ChatMessageRequestDto chatMessageRequestDto) {
        this.type = chatMessageRequestDto.getType();
        this.roomId = chatMessageRequestDto.getRoomId();
        this.sender = chatMessageRequestDto.getSender();
        this.senderImg = chatMessageRequestDto.getSenderImg();
        this.senderId = chatMessageRequestDto.getSenderId();
        this.message = chatMessageRequestDto.getMessage();
    }
}
