package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.model.Menu;
import com.mealchak.mealchakserverapplication.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = {"3. Menu controller"}) // Swagger
@RestController
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;
    // 모집글 불러오기
    @ApiOperation(value = "인기 메뉴 조회", notes = "인기 메뉴 조회")
    @GetMapping("/menu")
    public List<Menu> getPopularMenu() {
        return menuService.getPopular();
    }
}
