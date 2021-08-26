package com.mealchak.mealchakserverapplication.repository;


import com.mealchak.mealchakserverapplication.model.JoinRequests;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.mealchak.mealchakserverapplication.model.QJoinRequests.joinRequests;

@Repository
@RequiredArgsConstructor
public class JoinRequestQueryRepository {
    private final JPAQueryFactory queryFactory;

    public boolean existByUserId(Long userId) {
        return (queryFactory.selectFrom(joinRequests)
                .where(joinRequests.ownUserId.eq(userId))
                .fetchFirst() != null);
    }
}
