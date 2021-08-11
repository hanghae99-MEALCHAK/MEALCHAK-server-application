package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.response.MyAwaitRequestJoinResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.UserInfoAndPostResponseDto;
import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.*;
import com.mealchak.mealchakserverapplication.repository.mapping.UserInfoMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JoinRequestsService {

    private final PostRepository postRepository;
    private final JoinRequestsRepository joinRequestsRepository;
    private final UserRepository userRepository;
    private final AllChatInfoRepository allChatInfoRepository;
    private final ChatRoomRepository chatRoomRepository;

    //유저 신청정보 저장
    public String requestJoin(UserDetailsImpl userDetails, Long id) {
        Long userId = userDetails.getUser().getId();
        if (joinRequestsRepository.findByUserIdAndPostId(userId, id) == null) {
            Post post = postRepository.findById(id).orElseThrow(()
                    -> new IllegalArgumentException("해당 아이디를 찾을 수 없습니다."));
            User user = post.getUser();
            Long ownUserId = user.getId();
            JoinRequests joinRequests = new JoinRequests(userId, id, ownUserId);
            Long roomId = post.getChatRoom().getId();
            if (allChatInfoRepository.findByChatRoom_IdAndUser_Id(roomId,userId) == null) {
                joinRequestsRepository.save(joinRequests);
                return "신청완료";
            } else {
                return "이미 속해있는 채팅방입니다";
            }
        } else {
            return "이미 신청한 글입니다";
        }
    }

    //유저 신청정보 불러오기
    public List<UserInfoAndPostResponseDto> requestJoinList(UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        List<JoinRequests> joinRequestsList = joinRequestsRepository.findByOwnUserId(userId);
        List<UserInfoAndPostResponseDto> userInfoAndPostResponseDtoList = new ArrayList<>();

        for (JoinRequests joinRequests : joinRequestsList) {

            UserInfoMapping userInfoMapping = userRepository.findById(joinRequests.getUserId(), UserInfoMapping.class).orElseThrow(
                    () -> new IllegalArgumentException("회원이 아닙니다.")
            );

            Post post = postRepository.findById(joinRequests.getPostId()).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 게시글입니다.")
            );

            UserInfoAndPostResponseDto userInfoAndPostResponseDto = UserInfoAndPostResponseDto.builder()
                    .userId(userInfoMapping.getId())
                    .username(userInfoMapping.getUsername())
                    .profileImg(userInfoMapping.getProfileImg())
                    .postTitle(post.getTitle())
                    .joinRequestId(joinRequests.getId())
                    .build();
            userInfoAndPostResponseDtoList.add(userInfoAndPostResponseDto);
        }

        return userInfoAndPostResponseDtoList;
    }

    // 신청 승인 대기 리스트
    public List<MyAwaitRequestJoinResponseDto> myAwaitRequestJoinList(UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        List<JoinRequests> joinRequestsList = joinRequestsRepository.findByUserId(userId);
        List<MyAwaitRequestJoinResponseDto> myAwaitRequestJoinResponseDtoList = new ArrayList<>();
        for (JoinRequests joinRequests : joinRequestsList) {
            Post post = postRepository.findById(joinRequests.getPostId()).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 게시글입니다.")
            );
            MyAwaitRequestJoinResponseDto myAwaitRequestJoinResponseDto = MyAwaitRequestJoinResponseDto.builder()
                    .joinRequestId(joinRequests.getId())
                    .postTitle(post.getTitle())
                    .build();
            myAwaitRequestJoinResponseDtoList.add(myAwaitRequestJoinResponseDto);
        }
        return myAwaitRequestJoinResponseDtoList;
    }

    // 채팅방 참가 신청 승인/거절
    public String acceptJoinRequest(Long joinRequestId, boolean tOrF) {
        JoinRequests joinRequests = joinRequestsRepository.findById(joinRequestId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 신청입니다.")
        );
        if (!tOrF) {
            joinRequestsRepository.delete(joinRequests);
            return "거절되었습니다";
        }
        User user = userRepository.findById(joinRequests.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 유저입니다.")
        );
        Long postId = joinRequests.getPostId();
        if (!checkDuplicate(user, postId)) {
            if (checkHeadCount(postId)) {
                ChatRoom chatRoom = chatRoomRepository.findByPostId(postId);
                AllChatInfo allChatInfo = new AllChatInfo(user, chatRoom);
                allChatInfoRepository.save(allChatInfo);
                joinRequestsRepository.delete(joinRequests);
            } else {
                throw new IllegalArgumentException("채팅방 인원 초과");
            }
        }
        return "승인되었습니다";
    }

    // 채팅방 인원수 제한
    public Boolean checkHeadCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("postId가 존재하지 않습니다."));
        int postHeadCount = post.getHeadCount();
        Long nowHeadCount = allChatInfoRepository.countAllByChatRoom(post.getChatRoom());
        return postHeadCount > nowHeadCount;
    }

    // allChatInfo 테이블 중복생성금지
    public Boolean checkDuplicate(User user, Long postId) {
        List<AllChatInfo> allChatInfos = allChatInfoRepository.findAllByUserIdOrderByIdDesc(user.getId());
        for (AllChatInfo allChatInfo : allChatInfos) {
            if (allChatInfo.getChatRoom().getPost().getId().equals(postId)) {
                return true;
            }
        }
        return false;
    }

    // 채팅방 입장 신청 취소
    @Transactional
    public void requestJoinCancel(UserDetailsImpl userDetails, Long joinId) {
        Long userId = userDetails.getUser().getId();
        JoinRequests joinRequests = joinRequestsRepository.findByIdAndUserId(joinId, userId)
                .orElseThrow(()->new IllegalArgumentException("해당 신청이 존재하지 않습니다."));
        joinRequestsRepository.delete(joinRequests);
    }
}
