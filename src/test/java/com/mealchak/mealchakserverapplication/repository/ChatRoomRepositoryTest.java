package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(SpringExtension.class)
@DataJpaTest
class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("저장테스트")
    void findByPostId01() throws Exception {

        ChatRoom chatRoom = new ChatRoom("UUID", new User());
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        assertEquals(chatRoom.getId(), savedChatRoom.getId());
        assertEquals(chatRoom.getUuid(), savedChatRoom.getUuid());
        assertEquals(chatRoom.getOwnUserId(), savedChatRoom.getOwnUserId());

    }

//    @Test
//    @DisplayName("PostId로_채팅방_불러오기")
//    void findByPostId02() throws Exception {
//
//        ChatRoom chatRoom = new ChatRoom("UUID", new User());
//        chatRoomRepository.save(chatRoom);
//
//        ChatRoom savedChatRoom = chatRoomRepository.findByPostId(post.getId());
//
//
//    }

}