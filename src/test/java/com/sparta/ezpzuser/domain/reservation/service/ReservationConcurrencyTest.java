package com.sparta.ezpzuser.domain.reservation.service;

import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzuser.domain.reservation.dto.ReservationRequestDto;
import com.sparta.ezpzuser.domain.reservation.repository.ReservationRepository;
import com.sparta.ezpzuser.domain.slot.entity.Slot;
import com.sparta.ezpzuser.domain.slot.repository.SlotRepository;
import com.sparta.ezpzuser.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
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
    PopupRepository popupRepository;

    @Autowired
    SlotRepository slotRepository;

    User user = User.createMockUser();
    Popup popup;
    Slot slot;

    int threadCount = 100;
    int availableCount = 2;
    int totalCount = threadCount * availableCount;

    @BeforeEach
    void setUp() {
        popup = Popup.createMockPopup();
        popupRepository.save(popup);

        slot = Slot.createMockSlot(availableCount, totalCount, popup);
        slotRepository.save(slot);
    }

    @Test
    public void 예약_동시성_테스트_분산락_미적용() {
        // given
        given(reservationRepository.existsByUserIdAndPopupId(anyLong(), anyLong()))
                .willReturn(false);
        Long slotId = slot.getId();

        // when
        ReservationRequestDto requestDto = new ReservationRequestDto(slotId, availableCount);
        IntStream.range(0, threadCount).parallel().forEach(i ->
                reservationService.createReservationWithoutLock(requestDto, user)
        );

        // then
        int reservedCount = slotRepository.findById(slotId).orElseThrow().getReservedCount();
        assertThat(reservedCount).isNotZero().isNotEqualTo(threadCount);

        System.out.println("\n[reservedCount]");
        System.out.println("Expected = " + totalCount);
        System.out.println("Actual = " + reservedCount);
    }

    @Test
    public void 예약_동시성_테스트_분산락_적용() {
        // given
        given(reservationRepository.existsByUserIdAndPopupId(anyLong(), anyLong()))
                .willReturn(false);
        Long slotId = slot.getId();

        // when
        ReservationRequestDto requestDto = new ReservationRequestDto(slotId, availableCount);
        IntStream.range(0, threadCount).parallel().forEach(i ->
                reservationService.createReservation(requestDto, user)
        );

        // then
        int reservedCount = slotRepository.findById(slotId).orElseThrow().getReservedCount();
        assertThat(reservedCount).isEqualTo(totalCount);

        System.out.println("\n[reservedCount]");
        System.out.println("Expected = " + totalCount);
        System.out.println("Actual = " + reservedCount);
    }

}
