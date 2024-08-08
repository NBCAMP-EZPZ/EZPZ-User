package com.sparta.ezpzuser.domain.reservation.service;

import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzuser.domain.reservation.dto.ReservationRequestDto;
import com.sparta.ezpzuser.domain.reservation.repository.ReservationRepository;
import com.sparta.ezpzuser.domain.slot.entity.Slot;
import com.sparta.ezpzuser.domain.slot.enums.SlotStatus;
import com.sparta.ezpzuser.domain.slot.repository.SlotRepository;
import com.sparta.ezpzuser.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class ReservationConcurrencyTest {

    @Autowired
    ReservationService reservationService;

    @MockBean
    ReservationRepository reservationRepository;

    @Autowired
    SlotRepository slotRepository;

    @Autowired
    PopupRepository popupRepository;

    int threadCount = 100;
    int personCount = 2;
    int totalCount = threadCount * personCount;

    Popup popup;
    Slot slot;
    User user;

    @BeforeEach
    void setUp() {
        popup = popupRepository.save(Popup.createMockPopup());
        slot = slotRepository.save(Slot.createMockSlot(
                2, totalCount, popup, SlotStatus.PROCEEDING));
        user = User.createMockUser();
    }

    @Test
    @DisplayName("동시 예약 생성 테스트")
    public void 예약_생성_동시성_테스트() {
        // given
        given(reservationRepository.existsByUserIdAndPopupId(anyLong(), anyLong()))
                .willReturn(false);

        // when
        ReservationRequestDto requestDto = new ReservationRequestDto(slot.getId(), personCount);
        IntStream.range(0, threadCount).parallel().forEach(i ->
                reservationService.createReservation(requestDto, user)
        );

        // then
        int reservedCount = slotRepository.findById(slot.getId()).orElseThrow().getReservedCount();
        assertThat(reservedCount).isEqualTo(totalCount);
    }

}
