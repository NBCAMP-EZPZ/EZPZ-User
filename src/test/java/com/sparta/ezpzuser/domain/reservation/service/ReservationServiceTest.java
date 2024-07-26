package com.sparta.ezpzuser.domain.reservation.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import com.sparta.ezpzuser.domain.host.entity.Host;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.reservation.dto.ReservationRequestDto;
import com.sparta.ezpzuser.domain.reservation.dto.ReservationResponseDto;
import com.sparta.ezpzuser.domain.reservation.entity.Reservation;
import com.sparta.ezpzuser.domain.reservation.repository.ReservationRepository;
import com.sparta.ezpzuser.domain.slot.entity.Slot;
import com.sparta.ezpzuser.domain.slot.enums.SlotStatus;
import com.sparta.ezpzuser.domain.slot.repository.SlotRepository;
import com.sparta.ezpzuser.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ReservationServiceTest {
	@Mock
	ReservationRepository reservationRepository;
	
	@Mock
	SlotRepository slotRepository;
	
	@InjectMocks
	ReservationService reservationService;
	
	
	private User user;
	private Popup popup;
	private Host host;
	private Slot proceedingSlot;
	private Slot finishedSlot;
	private ReservationRequestDto reservationRequestDto;
	private Reservation reservation;
	
	@BeforeEach
	void setUp() {
		host = mock(Host.class);
		popup = mock(Popup.class);
		proceedingSlot = Slot.of(LocalDate.now(), LocalTime.now(), 50, popup, SlotStatus.PROCEEDING);
		finishedSlot = Slot.of(LocalDate.now(), LocalTime.now(), 50, popup, SlotStatus.FINISHED);
		setId(proceedingSlot, 1L);
		setId(finishedSlot, 2L);
		
		user = mock(User.class);
		
		reservationRequestDto = new ReservationRequestDto(1L, 2);
		
		reservation = Reservation.of(2, user, proceedingSlot);
		setId(reservation, 1L);
	}
	
	@Test
	@DisplayName("예약 등록 성공")
	void 예약등록성공() {
		//when
		when(popup.getId()).thenReturn(1L);
		when(user.getId()).thenReturn(1L);
		
		when(slotRepository.findSlotByIdWithPopup(anyLong())).thenReturn(Optional.of(proceedingSlot));
		when(reservationRepository.existsByUserIdAndPopupId(anyLong(), anyLong())).thenReturn(false);
		when(reservationRepository.save(any())).thenReturn(reservation);
		
		ReservationResponseDto request = reservationService.createReservation(reservationRequestDto, user);
		
		//then
		assertThat(request.getSlotDate()).isEqualTo(proceedingSlot.getSlotDate().toString());
		assertThat(request.getSlotTime()).isEqualTo(proceedingSlot.getSlotTime().toString());
		assertThat(request.getReservationStatus()).isEqualTo("READY");
	}
	
	@Test
	@DisplayName("예약 등록 실패 - 슬롯 없음")
	void 예약등록_실패_슬롯없음() {
	    //when
		when(slotRepository.findSlotByIdWithPopup(anyLong())).thenReturn(Optional.empty());
		
		CustomException exception = assertThrows(CustomException.class, () -> reservationService.createReservation(reservationRequestDto, user));
	    
	    //then
		assertThat(exception).isNotNull();
		assertThat(exception.getErrorType()).isEqualTo(ErrorType.SLOT_NOT_FOUND);
	}
	
	@Test
	@DisplayName("예약 등록 실패 - 이미 예약 내역 있음")
	void 예약등록_실패_예약내역존재() {
		//when
	    when(slotRepository.findSlotByIdWithPopup(anyLong())).thenReturn(Optional.of(proceedingSlot));
		when(reservationRepository.existsByUserIdAndPopupId(anyLong(), anyLong())).thenReturn(true);
		
		CustomException exception = assertThrows(CustomException.class, () -> reservationService.createReservation(reservationRequestDto, user));
		
	    //then
		assertThat(exception).isNotNull();
		assertThat(exception.getErrorType()).isEqualTo(ErrorType.RESERVATION_ALREADY_EXISTS);
	}
	
	@Test
	@DisplayName("예약 등록 실패 - 예약 가능 인원 초과")
	void 예약등록_실패_예약가능인원초과() {
	    //given
	    reservationRequestDto  = new ReservationRequestDto(1L, 5000);
		
	    //when
		when(slotRepository.findSlotByIdWithPopup(anyLong())).thenReturn(Optional.of(proceedingSlot));
		when(reservationRepository.existsByUserIdAndPopupId(anyLong(), anyLong())).thenReturn(false);
		
		CustomException exception = assertThrows(CustomException.class, () -> reservationService.createReservation(reservationRequestDto, user));
	    
	    //then
		assertThat(exception).isNotNull();
		assertThat(exception.getErrorType()).isEqualTo(ErrorType.RESERVATION_EXCEEDS_AVAILABLE_SLOTS);
	}
	
	@Test
	@DisplayName("예약 등록 실패 - 슬롯 예약 종료")
	void 예약등록_실패_슬롯예약종료() {
	    //when
		when(slotRepository.findSlotByIdWithPopup(anyLong())).thenReturn(Optional.of(finishedSlot));
		when(reservationRepository.existsByUserIdAndPopupId(anyLong(), anyLong())).thenReturn(false);
		
		CustomException exception = assertThrows(CustomException.class, () -> reservationService.createReservation(reservationRequestDto, user));
	    
	    //then
		assertThat(exception).isNotNull();
		assertThat(exception.getErrorType()).isEqualTo(ErrorType.SLOT_RESERVATION_FINISHED);
	}
	
	// Reflection을 사용하여 ID 설정하는 메소드
	private void setId(Object entity, Long id) {
		try {
			Field idField = entity.getClass().getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(entity, id);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
}