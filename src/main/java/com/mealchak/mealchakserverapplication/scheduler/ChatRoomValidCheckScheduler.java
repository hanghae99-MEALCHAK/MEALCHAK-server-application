package com.mealchak.mealchakserverapplication.scheduler;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoRepository;
import com.mealchak.mealchakserverapplication.repository.ChatRoomQueryRepository;
import com.mealchak.mealchakserverapplication.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomValidCheckScheduler {

    private final ChatRoomQueryRepository chatRoomQueryRepository;
    private final ChatRoomRepository chatRoomRepository;;
    private final AllChatInfoRepository allChatInfoRepository;

    //매일 0시에 실행
//    @Scheduled(cron = "0 0 00 * * ?")
    @Scheduled(fixedDelay = 3000 * 1000L)
    @Transactional
    @Async
    public void ChatRoomValidCheck() {
        List<ChatRoom> chatRoomList = chatRoomQueryRepository.findAllByChatValidFalse();
        for (ChatRoom chatRoom : chatRoomList) {
            List<AllChatInfo> allChatInfoList = allChatInfoRepository.findAllByChatRoom_Id(chatRoom.getId());
            for (AllChatInfo allChatInfo : allChatInfoList) {
                allChatInfoRepository.delete(allChatInfo);
            }
        }
    }
}
