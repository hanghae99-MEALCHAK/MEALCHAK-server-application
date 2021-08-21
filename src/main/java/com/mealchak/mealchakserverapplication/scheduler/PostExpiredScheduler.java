package com.mealchak.mealchakserverapplication.scheduler;

import com.mealchak.mealchakserverapplication.model.Post;
import com.mealchak.mealchakserverapplication.repository.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@RequiredArgsConstructor
@Service
public class PostExpiredScheduler {

    private final PostQueryRepository postQueryRepository;

    @Scheduled(cron ="0 0/1 * * * *")
    @Transactional
    @Async
    public void postValidationCheckScheduler() {
        List<Post> notExpiredList = postQueryRepository.findAllByCheckValidTrue();
        for (Post post : notExpiredList) {
            //orderTime 을 YYYY-MM-DD HH-MM-SS 형식으로 받았을때를 가정 (프론트와 합의후 추가 수정이 필요할수도있음)
            Date orderTime = java.sql.Timestamp.valueOf(post.getOrderTime());
            //현재시간을 Date 형으로 파싱
            Date nowTime = java.sql.Timestamp.valueOf(LocalDateTime.now());
            //현재시간이 orderTime 보다 이후라면 게시글의 유효여부를 false 로 변경
            if (nowTime.after(orderTime)) {
                post.expired(false);
            }
        }
    }
}
