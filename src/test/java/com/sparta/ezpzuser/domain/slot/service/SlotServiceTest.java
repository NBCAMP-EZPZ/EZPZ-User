//package com.sparta.ezpzuser.domain.slot.service;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.ActiveProfiles;
//
//import com.sparta.ezpzuser.common.exception.CustomException;
//import com.sparta.ezpzuser.common.exception.ErrorType;
//import com.sparta.ezpzuser.domain.popup.entity.Popup;
//import com.sparta.ezpzuser.domain.slot.dto.SlotResponseDto;
//import com.sparta.ezpzuser.domain.slot.entity.Slot;
//import com.sparta.ezpzuser.domain.slot.enums.SlotStatus;
//import com.sparta.ezpzuser.domain.slot.repository.SlotRepository;
//
//@ExtendWith(MockitoExtension.class)
//@ActiveProfiles("test")
//class SlotServiceTest {
//	@Mock
//	SlotRepository slotRepository;
//
//	@InjectMocks
//	SlotService slotService;
//
//	private Popup popup;
//	private Slot slot1;
//	private Slot slot2;
//	private Slot slot3;
//	private Pageable pageable;
//	private Page<Slot> slotPage;
//
//	@BeforeEach
//	void setUp() {
//		popup = mock(Popup.class);
//
//		LocalDate date = LocalDate.now();
//		LocalTime time = LocalTime.now();
//		slot1 = Slot.of(date, time, 1, popup, SlotStatus.PROCEEDING);
//		slot2 = Slot.of(date, time.plusHours(1), 1, popup, SlotStatus.PROCEEDING);
//		slot3 = Slot.of(date, time.plusHours(2), 1, popup, SlotStatus.PROCEEDING);
//
//
//		pageable = PageRequest.of(0, 3);
//		List<Slot> slots = List.of(slot1, slot2, slot3);
//		slotPage = new PageImpl<>(slots, pageable, slots.size());
//	}
//
//	@Test
//	@DisplayName("예약 가능한 슬롯 조회 테스트")
//	void 예약가능한슬롯조회() {
//	    //when
//		when(slotRepository.findByPopupId(anyLong(),any())).thenReturn(slotPage);
//
//		Page<SlotResponseDto> slotsByPopupId = slotService.findSlotsByPopupId(pageable, 1L);
//
//		//then
//		assertThat(slotsByPopupId).isNotNull();
//		assertThat(slotsByPopupId.getContent().size()).isEqualTo(3);
//	}
//
//	@Test
//	@DisplayName("예약 가능한 슬롯 조회 테스트 - 슬롯이 없는 경우")
//	void 예약가능한슬롯조회_실패_슬롯없음() {
//		//when
//		when(slotRepository.findByPopupId(anyLong(),any())).thenReturn(Page.empty());
//
//		CustomException exception = assertThrows(CustomException.class, () -> slotService.findSlotsByPopupId(pageable, 1L));
//
//	    //then
//		assertThat(exception.getErrorType()).isEqualTo(ErrorType.EMPTY_PAGE_ELEMENTS);
//	}
//
//	@Test
//	@DisplayName("예약 가능한 슬롯 조회 테스트 - 페이지가 없는 경우")
//	void 예약가능한슬롯조회_실패_페이지없음() {
//	    //given
//	    Pageable pageable = PageRequest.of(1, 3);
//
//	    //when
//		when(slotRepository.findByPopupId(anyLong(),any())).thenReturn(slotPage);
//
//		CustomException exception = assertThrows(CustomException.class, () -> slotService.findSlotsByPopupId(pageable, 1L));
//
//		//then
//		assertThat(exception.getErrorType()).isEqualTo(ErrorType.PAGE_NOT_FOUND);
//	}
//
//}