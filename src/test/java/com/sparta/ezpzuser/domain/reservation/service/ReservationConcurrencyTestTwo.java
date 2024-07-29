package com.sparta.ezpzuser.domain.reservation.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import com.sparta.ezpzuser.domain.host.entity.Host;
import com.sparta.ezpzuser.domain.host.repository.HostRepository;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzuser.domain.popup.enums.PopupStatus;
import com.sparta.ezpzuser.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzuser.domain.reservation.dto.ReservationRequestDto;
import com.sparta.ezpzuser.domain.reservation.entity.Reservation;
import com.sparta.ezpzuser.domain.reservation.repository.ReservationRepository;
import com.sparta.ezpzuser.domain.slot.entity.Slot;
import com.sparta.ezpzuser.domain.slot.enums.SlotStatus;
import com.sparta.ezpzuser.domain.slot.repository.SlotRepository;
import com.sparta.ezpzuser.domain.user.dto.SignupRequestDto;
import com.sparta.ezpzuser.domain.user.entity.User;
import com.sparta.ezpzuser.domain.user.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
public class ReservationConcurrencyTestTwo {
	@Autowired
	private PopupRepository popupRepository;
	@Autowired
	private HostRepository hostRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SlotRepository slotRepository;
	@Autowired
	private ReservationService reservationService;
	
	@TestConfiguration
	static class MockConfig {
		@Bean
		@Primary
		public ReservationService reservationService() {
			return new ReservationService(reservationRepository(), slotRepository());
		}
		
		public SlotRepository slotRepository() {
			SlotRepository slotRepository = mock(SlotRepository.class);
			
			when(slotRepository.findSlotByIdWithPopup(anyLong())).thenReturn(Optional.of(new Slot(LocalDate.now(), LocalTime.now(), 100,
				Popup.of(
					Host.of("username", "password", "test@email.com", "test", "test"),
				"name", "description", "location", "phone", "image", "manager", "010-1212-1212", PopupStatus.SCHEDULED, ApprovalStatus.APPROVED, LocalDateTime.now(), LocalDateTime.now()),
				SlotStatus.PROCEEDING)));
			
			return slotRepository;
		}
		
		public ReservationRepository reservationRepository() {
			ReservationRepository reservationRepository = mock(ReservationRepository.class);
			
			when(reservationRepository.existsByUserIdAndPopupId(anyLong(), anyLong())).thenReturn(false);
			when(reservationRepository.save(any(Reservation.class)))
				.thenReturn(Reservation.of(1,
					User.of(new SignupRequestDto(
						"test123123", "test123123", "test", "123@email.com", "010-1234-5678"
					), "encodedPassword"),
					Slot.of(LocalDate.now(), LocalTime.now(), 10,
						Popup.of(
							Host.of("username", "password", "test", "test", "test"),
					"name", "description", "location", "phone", "image", "manager", "010-1212-1212",
							PopupStatus.SCHEDULED, ApprovalStatus.APPROVED, LocalDateTime.now(), LocalDateTime.now()),
					SlotStatus.PROCEEDING)));
			
			return reservationRepository;
		}
	}
	
	private User user;
	private Slot slot;
	private Popup popup;
	private Host host;
	
	@BeforeEach
	public void setUp() {
		Host saveHost = Host.of("username", "password", "test", "test", "test");
		host = hostRepository.save(saveHost);
		
		Popup savePopup = Popup.of(host, "name", "description", "location", "phone", "image", "manager", "010-1212-1212", PopupStatus.SCHEDULED, ApprovalStatus.APPROVED, LocalDateTime.now(),
			LocalDateTime.now());
		popup = popupRepository.save(savePopup);
		
		Slot saveSlot = Slot.of(LocalDate.now(), LocalTime.now(), 100, popup, SlotStatus.PROCEEDING);
		slot = slotRepository.save(saveSlot);
		
		User saveUser = User.of(new SignupRequestDto(
			"test123123", "test123123", "test", "123@emai.com", "010-1234-5678"
		), "encodedPassword");
		user = userRepository.save(saveUser);
	}
	
	@Test
	@DisplayName("예약 생성 동시성 테스트")
	void 예약생성동시성테스트() throws InterruptedException {
	    //given
	    int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);
		
	    //when
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					reservationService.createReservation(new ReservationRequestDto(1L, 1), user);
				} finally {
					latch.countDown();
				}
			});
		}
		
		latch.await();
	    
	    //then
		int reservedCount = slotRepository.findById(1L).orElseThrow().getReservedCount();
		
		assertThat(reservedCount).isEqualTo(threadCount);
		System.out.println("\n[ReservationConcurrencyTestTwo] 예약 생성 동시성 테스트");
		System.out.println("requestCount = " + threadCount);
		System.out.println("reservedCount = " + reservedCount);
	}
}
