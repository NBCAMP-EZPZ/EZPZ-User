package com.sparta.ezpzuser.domain.like.service;

import com.sparta.ezpzuser.domain.host.entity.Host;
import com.sparta.ezpzuser.domain.host.repository.HostRepository;
import com.sparta.ezpzuser.domain.like.dto.LikeContentDto;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzuser.domain.popup.enums.PopupStatus;
import com.sparta.ezpzuser.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzuser.domain.user.dto.SignupRequestDto;
import com.sparta.ezpzuser.domain.user.entity.User;
import com.sparta.ezpzuser.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
public class LikeServiceTest {

    private static final Logger log = LoggerFactory.getLogger(LikeServiceTest.class);
    @Autowired
    private LikeService likeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HostRepository hostRepository;

    @Autowired
    private PopupRepository popupRepository;

    @BeforeEach
    void setUp() {
        List<User> userList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            SignupRequestDto requestDto = new SignupRequestDto(
                    "testUsers"+i,
                    "encodedPassword",
                    "Test Name",
                    "testUsers"+i+"@example.com",
                    "01012345678"
            );
            User user = User.of(requestDto, requestDto.getPassword());
            userList.add(user);
        }
        userRepository.saveAll(userList);

        Host host = new Host(
                1L,
                "hostUsername",
                "hostPassword",
                "host@example.com",
                "Host Company",
                "123-45-67890"
        );
        hostRepository.save(host);
        Popup popup = new Popup(
                1L,                    // id
                host,                  // host
                "Popup Name",          // name
                "Popup Description",   // description
                "http://example.com/thumbnail.jpg", // thumbnailUrl
                "thumbnail.jpg",       // thumbnailName
                "123 Popup St.",       // address
                "Manager Name",        // managerName
                "010-1234-5678",       // phoneNumber
                PopupStatus.IN_PROGRESS,    // popupStatus
                ApprovalStatus.APPROVED, // approvalStatus
                0,                     // likeCount
                LocalDateTime.now(),   // startDate
                LocalDateTime.now().plusDays(7) // endDate
        );
        popupRepository.save(popup);
    }

    @Test
    @DisplayName("좋아요 동시성 이슈 테스트")
    void contentLike() {
        Long requestCount = 100L;
        LikeContentDto contentDto = new LikeContentDto(1L, "popup");
        List<User> user = findUserList(requestCount);

        IntStream.range(0, 100).parallel()
                .forEach(i -> likeService.contentLike(contentDto, user.get(i)));

        Popup popup = popupRepository.findById(1L).orElse(null);
        assert popup != null;
        log.info("\nrequest Count : {}\nlike count : {}", requestCount, popup.getLikeCount());
    }

    private List<User> findUserList(Long count) {
        List<User> userList = new ArrayList<>();
        for (Long i = 1L; i <= count; i++) {
            userList.add(userRepository.findById(i).orElse(null));
        }
        return userList;
    }
}
