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

    public List<Post> findAllByCheckValidTrueOrderByOrderTimeAsc() {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .orderBy(post.orderTime.asc())
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .fetch();
    }

    public List<Post> findByCheckValidTrueAndMenu_CategoryOrderByOrderTimeAsc(String category) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.menu.category.eq(category))
                .orderBy(post.orderTime.asc())
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .fetch();
    }

    public List<Post> findByCheckValidTrueAndLocation_LatitudeBetweenAndLocation_LongitudeBetweenAndMenu_CategoryOrderByOrderTimeAsc(double latitudeStart,
                                                                                                                                     double latitudeEnd,
                                                                                                                                     double longitudeStart,
                                                                                                                                     double longitudeEnd,
                                                                                                                                     String category) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.menu.category.eq(category))
                .where(post.location.latitude.between(latitudeStart, latitudeEnd))
                .where(post.location.longitude.between(longitudeStart, longitudeEnd))
                .orderBy(post.orderTime.asc())
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .fetch();
    }


    public List<Post> findByCheckValidTrueAndLocation_LatitudeBetweenAndLocation_LongitudeBetweenOrderByOrderTimeAsc(double latitudeStart,
                                                                                                                     double latitudeEnd,
                                                                                                                     double longitudeStart,
                                                                                                                     double longitudeEnd) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.location.latitude.between(latitudeStart, latitudeEnd))
                .where(post.location.longitude.between(longitudeStart, longitudeEnd))
                .orderBy(post.orderTime.asc())
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .fetch();
    }

    public Post findByCheckValidTrueAndId(Long postId) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.id.eq(postId))
                .fetchOne();
    }

    public Post findByCheckValidTrueAndIdAndUserId(Long postId, Long userId) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.id.eq(postId))
                .where(post.user.id.eq(userId))
                .fetchOne();
    }

    public List<Post> findByCheckDeletedFalseAndUser_IdOrderByCreatedAtDesc(Long userId) {
        return queryFactory.selectFrom(post)
                .where(post.checkDeleted.eq(false))
                .where(post.user.id.eq(userId))
                .orderBy(post.createdAt.asc())
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
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

    public List<Post> findByCheckValidTrueAndTitleContainingOrCheckValidTrueAndContentsContainingOrCheckValidTrueAndLocation_AddressContaining(String keyword) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.title.contains(keyword)
                        .or(post.contents.contains(keyword))
                        .or(post.location.address.contains(keyword))
                )
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .fetch();
    }

    public List<Post> findByCheckValidTrueAndTitleContainingOrCheckValidTrueAndContentsContainingOrCheckValidTrueAndLocation_AddressContainingOrderByOrderTimeAsc(String keyword) {
        return queryFactory.selectFrom(post)
                .where(post.checkValid.eq(true))
                .where(post.title.contains(keyword)
                        .or(post.contents.contains(keyword))
                        .or(post.location.address.contains(keyword))
                )
                .orderBy(post.orderTime.asc())
                .join(post.user)
                .join(post.menu)
                .join(post.chatRoom)
                .fetchJoin()
                .fetch();
    }

}
