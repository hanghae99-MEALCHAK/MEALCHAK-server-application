package com.mealchak.mealchakserverapplication.repository;


import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.mealchak.mealchakserverapplication.model.QAllChatInfo.allChatInfo;

@RequiredArgsConstructor
@Repository
public class AllChatInfoQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Long countAllByChatRoom(ChatRoom chatRoom){
        return queryFactory.selectFrom(allChatInfo)
                .where(allChatInfo.chatRoom.eq(chatRoom))
                .join(allChatInfo.chatRoom)
                .join(allChatInfo.user)
                .fetchJoin()
                .fetchCount();
    }

    public AllChatInfo findByChatRoom_IdAndUser_Id(Long roomId, Long userId){
        return queryFactory.selectFrom(allChatInfo)
                .where(allChatInfo.chatRoom.id.eq(roomId))
                .where(allChatInfo.user.id.eq(userId))
                .join(allChatInfo.chatRoom)
                .join(allChatInfo.user)
                .fetchJoin()
                .fetchOne();
    }

    public List<AllChatInfo> findAllByChatRoom_Id(Long roomId){
        return queryFactory.selectFrom(allChatInfo)
                .where(allChatInfo.chatRoom.id.eq(roomId))
                .join(allChatInfo.chatRoom)
                .join(allChatInfo.user)
                .fetchJoin()
                .fetch();
    }

    public List<AllChatInfo> findAllByUserIdOrderByIdDesc(Long userId){
        return queryFactory.selectFrom(allChatInfo)
                .where(allChatInfo.user.id.eq(userId))
                .orderBy(allChatInfo.id.desc())
                .join(allChatInfo.chatRoom)
                .join(allChatInfo.user)
                .fetchJoin()
                .fetch();
    }
}
