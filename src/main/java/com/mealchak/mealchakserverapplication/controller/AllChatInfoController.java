package com.mealchak.mealchakserverapplication.controller;


import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.mapping.UserInfoMapping;
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
    public List<UserInfoMapping> getUser(@PathVariable Long roomId,UserDetailsImpl userDetails){
        return allChatInfoService.getUser(roomId,userDetails);
    }

}
