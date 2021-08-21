package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.Menu;
import com.mealchak.mealchakserverapplication.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MenuService {
    private final MenuRepository menuRepository;

    // 인기메뉴 3종류 조회
    public List<Menu> getPopular() {
         return menuRepository.findAllByOrderByCountDesc().subList(0,3);
    }
}
