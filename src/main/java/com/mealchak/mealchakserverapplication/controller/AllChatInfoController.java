package com.mealchak.mealchakserverapplication.controller;


import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.service.AllChatInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = {"4. 채팅방 유저목록"}) // Swagger
public class AllChatInfoController {

    private final AllChatInfoService allChatInfoService;

    @ApiOperation(value = "채팅방 유저목록", notes = "채팅방 유저목록")
    @GetMapping("/chat/user/{roomId}")
    public List<User> getUser(@PathVariable Long roomId){
        return allChatInfoService.getUser(roomId);
    }

}
