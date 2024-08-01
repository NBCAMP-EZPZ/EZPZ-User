package com.sparta.ezpzuser.domain.reservation.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.sparta.ezpzuser.domain.host.entity.Host;
import com.sparta.ezpzuser.domain.host.repository.HostRepository;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzuser.domain.popup.enums.PopupStatus;
import com.sparta.ezpzuser.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzuser.domain.reservation.dto.ReservationRequestDto;
import com.sparta.ezpzuser.domain.reservation.dto.ReservationResponseDto;
import com.sparta.ezpzuser.domain.slot.entity.Slot;
import com.sparta.ezpzuser.domain.slot.enums.SlotStatus;
import com.sparta.ezpzuser.domain.slot.repository.SlotRepository;
import com.sparta.ezpzuser.domain.user.dto.SignupRequestDto;
import com.sparta.ezpzuser.domain.user.entity.User;
import com.sparta.ezpzuser.domain.user.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
public class ReservationConcurrencyTest {
	@Autowired
	private ReservationService reservationService;
	
	@Autowired
	private SlotRepository slotRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PopupRepository popupRepository;
	
	@Autowired
	private HostRepository hostRepository;
	
	private Slot slot;
	private User user;
	private Popup popup;
	private Host host;
	
	public void init() {
		host = hostRepository.save(Host.of("test123", "test123", "123@email.com", "group", "010-1234-5678"));
		
		popup = popupRepository.save(Popup.of(host, "test", "test", "test", "test", "address", "heesue", "010-1234-5678", PopupStatus.SCHEDULED, ApprovalStatus.APPROVED, LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
		
		Slot saveSlot = slotRepository.save(Slot.of(LocalDate.now(), LocalTime.now(), 100, popup, SlotStatus.PROCEEDING));
		slot = saveSlot;
		System.out.println("saveSlot = " + saveSlot);
	}
	
	@Test
	@DisplayName("단일 예약 테스트")
	void 예약_생성_단일_테스트() {
	    //given
		init();
		SignupRequestDto signupRequestDto = new SignupRequestDto("test123123", "test123123", "test", "test@email.com", "010-5284-6797");
		user = userRepository.save(User.of(signupRequestDto, "test123123"));
		
		ReservationRequestDto requestDto = new ReservationRequestDto(slot.getId(), 1);
		
		//when
		ReservationResponseDto reservation = reservationService.createReservation(requestDto, user);
		
		//then
		assertThat(reservation.getId()).isNotNull();
		assertThat(reservation.getSlotTime()).isEqualTo(slot.getSlotTime().toString());
		assertThat(slot.getReservedCount()).isEqualTo(1);
	}
	
	@Test
	@DisplayName("동시 예약 생성 테스트")
	public void 예약_생성_동시성_테스트() throws InterruptedException {
		init();
		int numberOfThreads = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);
		
		for (int i = 0; i < numberOfThreads; i++) {
			executorService.execute(() -> {
				try {
					SignupRequestDto signupRequestDto = new SignupRequestDto("test123123", "test123123", "test", "test@email.com", "010-5284-6797");
					user = userRepository.saveAndFlush(User.of(signupRequestDto, "test123123"));
					
					ReservationRequestDto requestDto = new ReservationRequestDto(slot.getId(), 1);
					reservationService.createReservation(requestDto, user);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					latch.countDown();
				}
			});
		}
		
		latch.await();
		executorService.shutdown();
		
		Slot updatedSlot = slotRepository.findById(slot.getId()).orElseThrow();
		
		System.out.println("requestCount = " + numberOfThreads);
		System.out.println("reservedCount = " + updatedSlot.getReservedCount());
		
		assertThat(updatedSlot.getReservedCount()).isEqualTo(numberOfThreads);
	}
}
