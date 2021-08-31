package com.mealchak.mealchakserverapplication.scheduler;

import com.mealchak.mealchakserverapplication.model.Menu;
import com.mealchak.mealchakserverapplication.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PopularMenuResetScheduler {

    private final MenuRepository menuRepository;

    //매일 0시에 실행
    @Scheduled(cron = "0 0 00 * * ?")
    @Transactional
    @Async
    // 메뉴의 인기 순위를 매일 0시마다 초기화함
    public void popularMenuCountReset(){
        List<Menu> menuList = menuRepository.findAll();
        for (Menu menu : menuList){
            menu.resetMenuCount();
        }
    }
}
