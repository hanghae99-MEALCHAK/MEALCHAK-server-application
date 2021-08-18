package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.mealchak.mealchakserverapplication.model.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<Post> findAllByCheckValidTrueOrderByOrderTimeAsc(){
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .orderBy(post.orderTime.asc())
                .fetch();
    }
}
