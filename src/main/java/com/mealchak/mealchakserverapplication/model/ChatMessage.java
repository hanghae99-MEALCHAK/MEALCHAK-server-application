package com.mealchak.mealchakserverapplication.model;

import com.mealchak.mealchakserverapplication.dto.request.ChatMessageCreateRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class ChatMessage extends Timestamped {

    public enum MessageType {
        ENTER, TALK, QUIT
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long messageId;

    @Column
    private MessageType type;

    @Column
    private Long roomId;

    @Column
    private String username;

    @Column
    private String message;

    public ChatMessage(ChatMessageCreateRequestDto requestDto) {
        this.type = requestDto.getType();
        this.username = requestDto.getUsername();
        this.message = requestDto.getMessage();
        this.roomId = requestDto.getRoomId();
    }
}
