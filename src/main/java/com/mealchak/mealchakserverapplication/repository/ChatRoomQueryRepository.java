package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.mealchak.mealchakserverapplication.model.QChatRoom.chatRoom;

@Repository
@RequiredArgsConstructor
public class ChatRoomQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<ChatRoom> findAllByChatValidFalse(){
        return queryFactory.selectFrom(chatRoom)
                .where(chatRoom.chatValid.eq(false))
                .join(chatRoom.post)
                .fetchJoin()
                .fetch();
    }
}
