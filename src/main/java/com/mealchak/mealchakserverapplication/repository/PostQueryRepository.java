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

    public List<Post> findAllOrderByOrderTimeAsc() {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .orderBy(post.orderTime.asc())
                .fetch();
    }

    public List<Post> findByMenu_CategoryOrderByOrderTimeAsc(String category) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.menu.category.eq(category))
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .orderBy(post.orderTime.asc())
                .fetch();
    }

    public List<Post> findByLocationAndCategoryOrderByOrderTimeAsc(double latitudeStart,
                                                                   double latitudeEnd,
                                                                   double longitudeStart,
                                                                   double longitudeEnd,
                                                                   String category) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.menu.category.eq(category))
                .where(post.location.latitude.between(latitudeStart, latitudeEnd))
                .where(post.location.longitude.between(longitudeStart, longitudeEnd))
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .orderBy(post.orderTime.asc())
                .fetch();
    }


    public List<Post> findByLocationOrderByOrderTimeAsc(double latitudeStart,
                                                        double latitudeEnd,
                                                        double longitudeStart,
                                                        double longitudeEnd) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.location.latitude.between(latitudeStart, latitudeEnd))
                .where(post.location.longitude.between(longitudeStart, longitudeEnd))
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .orderBy(post.orderTime.asc())
                .fetch();
    }

    public Post findByCheckValidTrueAndId(Long postId) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.id.eq(postId))
                .fetchOne();
    }

    public Post findByIdAndUserId(Long postId, Long userId) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.id.eq(postId))
                .where(post.user.id.eq(userId))
                .fetchOne();
    }

    public List<Post> findByUser_IdOrderByCreatedAtDesc(Long userId) {
        return queryFactory.selectFrom(post)
                .where(post.checkDeleted.eq(false))
                .where(post.user.id.eq(userId))
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .orderBy(post.createdAt.desc())
                .fetch();
    }

    public List<Post> findAllByCheckValidTrue() {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .fetch();
    }

    public List<Post> findBySearchKeyword(String keyword) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.title.contains(keyword)
                        .or(post.contents.contains(keyword))
                )
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .fetch();
    }

    public List<Post> findBySearchKeywordOrderByOrderTimeAsc(String keyword) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.title.contains(keyword)
                        .or(post.contents.contains(keyword))
                )
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .orderBy(post.orderTime.asc())
                .fetch();
    }

    public Post findById(Long id){
        return queryFactory.selectFrom(post)
                .where(post.id.eq(id))
                .fetchOne();
    }
}
