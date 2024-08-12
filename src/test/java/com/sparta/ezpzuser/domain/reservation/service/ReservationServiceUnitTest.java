// package com.sparta.ezpzuser.domain.reservation.service;
//
// import com.sparta.ezpzuser.common.entity.RestPage;
// import com.sparta.ezpzuser.common.exception.CustomException;
// import com.sparta.ezpzuser.domain.popup.entity.Popup;
// import com.sparta.ezpzuser.domain.reservation.dto.ReservationRequestDto;
// import com.sparta.ezpzuser.domain.reservation.dto.ReservationResponseDto;
// import com.sparta.ezpzuser.domain.reservation.entity.Reservation;
// import com.sparta.ezpzuser.domain.reservation.enums.ReservationStatus;
// import com.sparta.ezpzuser.domain.reservation.repository.ReservationRepository;
// import com.sparta.ezpzuser.domain.slot.entity.Slot;
// import com.sparta.ezpzuser.domain.slot.enums.SlotStatus;
// import com.sparta.ezpzuser.domain.slot.repository.SlotRepository;
// import com.sparta.ezpzuser.domain.user.entity.User;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
//
// import java.lang.reflect.Field;
// import java.util.List;
// import java.util.Optional;
//
// import static com.sparta.ezpzuser.common.exception.ErrorType.*;
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.BDDMockito.given;
// import static org.mockito.Mockito.*;
//
// @ExtendWith(MockitoExtension.class)
// class ReservationServiceUnitTest {
//
//     @Mock
//     private ReservationRepository reservationRepository;
//
//     @Mock
//     private SlotRepository slotRepository;
//
//     @InjectMocks
//     private ReservationService reservationService;
//
//     private User user;
//
//     private Slot proceedingSlot;
//     private Slot finishedSlot;
//
//     private ReservationRequestDto requestDto;
//     private Reservation reservation;
//
//     private final int availableCount = 2;
//
//     @BeforeEach
//     void setUp() {
//         user = mock(User.class);
//         Popup popup = mock(Popup.class);
//
//         proceedingSlot = Slot.createMockSlot(availableCount, 50, popup, SlotStatus.PROCEEDING);
//         finishedSlot = Slot.createMockSlot(availableCount, 50, popup, SlotStatus.FINISHED);
//
//         setId(proceedingSlot, 1L);
//         setId(finishedSlot, 2L);
//
//         requestDto = new ReservationRequestDto(1L, availableCount);
//
//         reservation = Reservation.of(availableCount, user, proceedingSlot);
//         setId(reservation, 1L);
//     }
//
//     @Test
//     void 예약_성공() {
//         // given
//         given(slotRepository.findSlotByIdWithPopup(anyLong()))
//                 .willReturn(Optional.of(proceedingSlot));
//
//         given(reservationRepository.existsByUserIdAndPopupId(anyLong(), anyLong()))
//                 .willReturn(false);
//
//         // when
//         ReservationResponseDto responseDto = reservationService.createReservation(requestDto, user);
//
//         // then
//         assertThat(proceedingSlot.getReservedCount()).isEqualTo(availableCount);
//         assertThat(responseDto.getSlotDate()).isEqualTo(proceedingSlot.getSlotDate().toString());
//         assertThat(responseDto.getSlotTime()).isEqualTo(proceedingSlot.getSlotTime().toString());
//         assertThat(responseDto.getReservationStatus()).isEqualTo("READY");
//     }
//
//     @Test
//     void 예약_실패_슬롯_없음() {
//         // given
//         given(slotRepository.findSlotByIdWithPopup(anyLong()))
//                 .willReturn(Optional.empty());
//
//         // when
//         CustomException exception = assertThrows(CustomException.class, () ->
//                 reservationService.createReservation(requestDto, user)
//         );
//
//         // then
//         assertThat(exception.getErrorType()).isEqualTo(SLOT_NOT_FOUND);
//     }
//
//     @Test
//     void 예약_실패_이미_예약함() {
//         // given
//         given(slotRepository.findSlotByIdWithPopup(anyLong()))
//                 .willReturn(Optional.of(proceedingSlot));
//
//         given(reservationRepository.existsByUserIdAndPopupId(anyLong(), anyLong()))
//                 .willReturn(true);
//
//         // when
//         CustomException exception = assertThrows(CustomException.class, () ->
//                 reservationService.createReservation(requestDto, user)
//         );
//
//         // then
//         assertThat(exception.getErrorType()).isEqualTo(RESERVATION_ALREADY_EXISTS);
//     }
//
//     @Test
//     void 예약_실패_예약_가능_인원_초과() {
//         // given
//         given(slotRepository.findSlotByIdWithPopup(anyLong()))
//                 .willReturn(Optional.of(proceedingSlot));
//
//         given(reservationRepository.existsByUserIdAndPopupId(anyLong(), anyLong()))
//                 .willReturn(false);
//
//         // when
//         ReservationRequestDto requestDto = new ReservationRequestDto(anyLong(), availableCount + 1);
//         CustomException exception = assertThrows(CustomException.class, () ->
//                 reservationService.createReservation(requestDto, user)
//         );
//
//         // then
//         assertThat(exception.getErrorType()).isEqualTo(RESERVATION_EXCEEDS_AVAILABLE_SLOTS);
//     }
//
//     @Test
//     void 예약_실패_슬롯_예약_진행_중_아님() {
//         // given
//         given(slotRepository.findSlotByIdWithPopup(anyLong()))
//                 .willReturn(Optional.of(finishedSlot));
//
//         given(reservationRepository.existsByUserIdAndPopupId(anyLong(), anyLong()))
//                 .willReturn(false);
//
//         // when
//         CustomException exception = assertThrows(CustomException.class, () ->
//                 reservationService.createReservation(requestDto, user)
//         );
//
//         // then
//         assertThat(exception.getErrorType()).isEqualTo(SLOT_RESERVATION_CLOSED);
//     }
//
//     @Test
//     void 예약_목록_조회_성공() {
//         // given
//         Pageable pageable = PageRequest.of(0, 10);
//         List<Reservation> reservations = List.of(reservation);
//         RestPage<Reservation> reservationPage = new RestPage<>(new PageImpl<>(reservations, pageable, 1L));
//         given(reservationRepository.findByUserIdAndStatus(anyLong(), any(), any()))
//                 .willReturn(reservationPage);
//
//         // when
//         Page<ReservationResponseDto> result = reservationService.findAllReservations(pageable, "READY", user);
//
//         // then
//         assertThat(result).isNotNull();
//         assertThat(result.getContent().size()).isEqualTo(1);
//     }
//
//     @Test
//     void 예약_목록_조회_실패_페이지_없음() {
//         // given
//         Pageable pageable = PageRequest.of(0, 10);
//         given(reservationRepository.findByUserIdAndStatus(anyLong(), any(), any()))
//                 .willReturn(new RestPage<>(new PageImpl<>(List.of())));
//
//         // when
//         CustomException exception = assertThrows(CustomException.class, () ->
//                 reservationService.findAllReservations(pageable, "READY", user)
//         );
//
//         // then
//         assertThat(exception.getErrorType()).isEqualTo(EMPTY_PAGE_ELEMENTS);
//     }
//
//     @Test
//     void 예약_상세_조회_성공() {
//         // given
//         given(reservationRepository.findReservationAndSlotPopUp(anyLong(), anyLong()))
//                 .willReturn(Optional.of(reservation));
//
//         // when
//         ReservationResponseDto result = reservationService.findReservation(1L, user);
//
//         // then
//         assertThat(result.getId()).isEqualTo(1L);
//         assertThat(result.getSlotDate()).isEqualTo(reservation.getSlot().getSlotDate().toString());
//     }
//
//     @Test
//     @DisplayName("예약 상세 조회 실패 - 예약 없음")
//     void 예약상세조회_실패_예약없음() {
//         //when
//         when(reservationRepository.findReservationAndSlotPopUp(anyLong(), anyLong())).thenReturn(Optional.empty());
//
//         CustomException exception = assertThrows(CustomException.class, () -> reservationService.findReservation(1L, user));
//
//         //then
//         assertThat(exception).isNotNull();
//         assertThat(exception.getErrorType()).isEqualTo(RESERVATION_NOT_FOUND);
//     }
//
//     @Test
//     @DisplayName("예약 취소 성공")
//     void 예약취소성공() {
//         //when
//         when(reservationRepository.findReservationAndSlot(anyLong(), anyLong()))
//                 .thenReturn(Optional.of(reservation));
//
//         reservationService.cancelReservation(1L, user);
//
//         //then
//         assertThat(reservation.getReservationStatus()).isEqualTo(ReservationStatus.CANCEL);
//         assertThat(reservation.getSlot().getReservedCount()).isEqualTo(-2);
//         verify(slotRepository).save(reservation.getSlot());
//     }
//
//     @Test
//     @DisplayName("예약 취소 실패 - 예약 없음")
//     void 예약취소_실패_예약없음() {
//         //when
//         when(reservationRepository.findReservationAndSlot(anyLong(), anyLong())).thenReturn(Optional.empty());
//
//         CustomException exception = assertThrows(CustomException.class, () -> reservationService.cancelReservation(1L, user));
//
//         //then
//         assertThat(exception).isNotNull();
//         assertThat(exception.getErrorType()).isEqualTo(RESERVATION_NOT_FOUND);
//     }
//
//
//     // Reflection을 사용하여 ID 설정하는 메소드
//     private void setId(Object entity, Long id) {
//         try {
//             Field idField = entity.getClass().getDeclaredField("id");
//             idField.setAccessible(true);
//             idField.set(entity, id);
//         } catch (NoSuchFieldException | IllegalAccessException e) {
//             throw new RuntimeException(e);
//         }
//     }
//
// }