package com.mealchak.mealchakserverapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealchak.mealchakserverapplication.MockSpringSecurityFilter;
import com.mealchak.mealchakserverapplication.config.WebSecurityConfig;
import com.mealchak.mealchakserverapplication.dto.request.ReviewRequestDto;
import com.mealchak.mealchakserverapplication.model.Review;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.ReviewRepository;
import com.mealchak.mealchakserverapplication.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.Collections;

import static com.mealchak.mealchakserverapplication.model.Review.MannerType.BEST;
import static com.mealchak.mealchakserverapplication.model.Review.MannerType.GOOD;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ReviewController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
@MockBean(JpaMetamodelMappingContext.class)
class ReviewControllerTest {

    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private ReviewRepository reviewRepository;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();

        // Create mock principal for the test user
        User testUser = new User(102L, 103L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남", "ㅎㅇ", 50f, null);
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", Collections.emptyList());
    }

    @Test
    @DisplayName("리뷰 생성")
    public void createReview() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto("review_test_create", BEST);

        String reviewInfo = objectMapper.writeValueAsString(requestDto);

        mvc.perform(post("/review/{userId}", 102)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewInfo)
                        .principal(mockPrincipal)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 조회")
    public void getReview() throws Exception {
        User user = new User(102L, 103L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남", "ㅎㅇ", 50f, null);
        User writer = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남", "ㅎㅇ", 50f, null);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Review review = new Review(90L, "review_test", user, writer, GOOD);
    }

    @Test
    @DisplayName("리뷰 수정")
    public void updateReview() throws Exception {
        User user = new User(102L, 103L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남", "ㅎㅇ", 50f, null);
        User writer = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남", "ㅎㅇ", 50f, null);
        ReviewRequestDto requestDto = new ReviewRequestDto("review_test_update", GOOD);

        String reviewInfo = objectMapper.writeValueAsString(requestDto);

        mvc.perform(put("/review/{reviewId}", 90)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewInfo)
                        .principal(mockPrincipal)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 삭제")
    public void deleteReview() throws Exception {
        User user = new User(102L, 103L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남", "ㅎㅇ", 50f, null);
        User writer = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남", "ㅎㅇ", 50f, null);
        Review review = new Review(90L, "review_test", user, writer, BEST);

        mvc.perform(delete("/review/{reviewId}", 90)
                        .principal(mockPrincipal)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}