package com.sparta.ezpzuser.domain.slot.entity;

import com.sparta.ezpzuser.common.exception.CustomException;
import org.junit.jupiter.api.Test;

import static com.sparta.ezpzuser.common.exception.ErrorType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SlotTest {

    @Test
    void 예약_가능_여부_검증_미진행_슬롯() {
        // given
        int availableCount = 2;
        Slot slot = Slot.createMockFinishedSlot(availableCount, 10, null);

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                slot.verifyReservationAvailability(availableCount)
        );

        // then
        assertThat(exception.getErrorType()).isEqualTo(SLOT_RESERVATION_CLOSED);
    }

    @Test
    void 예약_가능_여부_검증_예약_만석() {
        // given
        int availableCount = 2;
        Slot slot = Slot.createMockSlot(availableCount, 10, 10, null);

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                slot.verifyReservationAvailability(availableCount)
        );

        // then
        assertThat(exception.getErrorType()).isEqualTo(SLOT_FULL);
    }

    @Test
    void 예약_가능_여부_검증_예약_인원_무효() {
        // given
        int availableCount = 2;
        Slot slot = Slot.createMockSlot(availableCount, 10, null);

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                slot.verifyReservationAvailability(availableCount + 1)
        );

        // then
        assertThat(exception.getErrorType()).isEqualTo(RESERVATION_EXCEEDS_AVAILABLE_SLOTS);
    }

    @Test
    void 예약_가능_여부_검증_예약_인원_초과() {
        // given
        int availableCount = 2;
        Slot slot = Slot.createMockSlot(availableCount, 10, 9, null);

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                slot.verifyReservationAvailability(availableCount)
        );

        // then
        assertThat(exception.getErrorType()).isEqualTo(RESERVATION_EXCEEDS_AVAILABLE_SLOTS);
    }

    @Test
    void 예약_개수_증가_성공() {
        // given
        int availableCount = 2;
        int reservedCount = 8;
        Slot slot = Slot.createMockSlot(availableCount, 10, reservedCount, null);

        // when
        slot.increaseReservedCount(availableCount);

        // then
        assertThat(slot.getReservedCount()).isEqualTo(reservedCount + availableCount);
    }

    @Test
    void 예약_개수_증가_실패() {
        // given
        int availableCount = 2;
        int totalCount = 10;
        int reservedCount = totalCount - availableCount + 1;
        Slot slot = Slot.createMockSlot(availableCount, totalCount, reservedCount, null);

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                slot.increaseReservedCount(availableCount)
        );

        // then
        assertThat(exception.getErrorType()).isEqualTo(EXCEED_TOTAL_COUNT);
    }

    @Test
    void 예약_개수_감소_성공() {
        // given
        int availableCount = 2;
        int reservedCount = 10;
        Slot slot = Slot.createMockSlot(availableCount, 10, reservedCount, null);

        // when
        slot.decreaseReservedCount(availableCount);

        // then
        assertThat(slot.getReservedCount()).isEqualTo(reservedCount - availableCount);
    }

    @Test
    void 예약_개수_감소_실패() {
        // given
        int availableCount = 2;
        int totalCount = 10;
        int reservedCount = availableCount - 1;
        Slot slot = Slot.createMockSlot(availableCount, totalCount, reservedCount, null);

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                slot.decreaseReservedCount(availableCount)
        );

        // then
        assertThat(exception.getErrorType()).isEqualTo(RESERVED_COUNT_DEFICIENT);
    }

}