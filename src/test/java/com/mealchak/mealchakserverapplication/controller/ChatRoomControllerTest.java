package com.mealchak.mealchakserverapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealchak.mealchakserverapplication.MockSpringSecurityFilter;
import com.mealchak.mealchakserverapplication.config.WebSecurityConfig;
import com.mealchak.mealchakserverapplication.dto.response.ChatRoomListResponseDto;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.service.ChatMessageService;
import com.mealchak.mealchakserverapplication.service.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ChatRoomController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
@MockBean(JpaMetamodelMappingContext.class)
class ChatRoomControllerTest {

    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private ChatRoomService chatRoomService;

    @MockBean
    private ChatMessageService chatMessageService;

    private UserDetailsImpl testUserDetails;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();

        // Create mock principal for the test user
        User testUser = new User("test-username", "test-pwd");
        testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", Collections.emptyList());

    }

    @Test
    @DisplayName("채팅방_사용자별_목록_조회")
    void getOnesChatRoom() throws Exception {
        List<ChatRoomListResponseDto> chatRoomListResponseDtoList = new ArrayList<>();

        when(chatRoomService.getOnesChatRoom(testUserDetails.getUser())).thenReturn(chatRoomListResponseDtoList);

        mvc.perform(get("/chat/rooms/mine")
                .principal(mockPrincipal)
        )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        verify(chatRoomService, times(1)).getOnesChatRoom(testUserDetails.getUser());
    }

    @Test
    @DisplayName("해당 채팅방 나가기")
    void quitChat() throws Exception {
        mvc.perform(delete("/chat/quit/{postId}", 100L)
                .principal(mockPrincipal)
        )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        verify(chatRoomService, times(1)).quitChat(100L, testUserDetails);
    }


}