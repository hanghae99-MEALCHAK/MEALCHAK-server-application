package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.Menu;
import com.mealchak.mealchakserverapplication.repository.MenuRepository;
import com.mealchak.mealchakserverapplication.service.MenuService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Test
    public void 인기메뉴불러오기() {
        // given
        List<Menu> menuList = new ArrayList<>();
        Menu menu1 = new Menu("중식", 4);
        Menu menu2 = new Menu("일식", 3);
        Menu menu3 = new Menu("한식", 2);
        Menu menu4 = new Menu("양식", 1);
        menuList.add(menu1);
        menuList.add(menu2);
        menuList.add(menu3);
        menuList.add(menu4);

        // mocking
        when(menuRepository.findAllByOrderByCountDesc())
                .thenReturn(menuList);

        // when
        List<Menu> result = menuService.getPopular();

        // then
        assertEquals(result.get(0).getCategory(), menuList.get(0).getCategory());
        assertEquals(result.get(0).getCount(), menuList.get(0).getCount());
        assertEquals(result.get(1).getCategory(), menuList.get(1).getCategory());
        assertEquals(result.get(1).getCount(), menuList.get(1).getCount());
        assertEquals(result.get(2).getCategory(), menuList.get(2).getCategory());
        assertEquals(result.get(2).getCount(), menuList.get(2).getCount());

        assertEquals(result.size(), 3);

        verify(menuRepository).findAllByOrderByCountDesc();

    }
}