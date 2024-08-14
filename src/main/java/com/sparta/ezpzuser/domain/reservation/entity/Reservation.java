package com.sparta.ezpzuser.domain.reservation.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.domain.reservation.enums.ReservationStatus;
import com.sparta.ezpzuser.domain.slot.entity.Slot;
import com.sparta.ezpzuser.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.sparta.ezpzuser.common.exception.ErrorType.UNVISITED_USER;
import static com.sparta.ezpzuser.common.exception.ErrorType.WRONG_RESERVED_USER;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation", indexes = {
        @Index(name = "idx_reservation_user_status", columnList = "user_id, reservation_status, slot_id")
})
public class Reservation extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    private int numberOfPersons;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id")
    private Slot slot;

    private Reservation(int numberOfPersons, User user, Slot slot) {
        this.numberOfPersons = numberOfPersons;
        this.reservationStatus = ReservationStatus.READY;
        this.user = user;
        this.slot = slot;
    }

    public static Reservation of(int numberOfPersons, User user, Slot slot) {
        return new Reservation(numberOfPersons, user, slot);
    }

    /**
     * 예약 취소
     */
    public void cancel() {
        this.reservationStatus = ReservationStatus.CANCEL;
    }

    /**
     * 해당 팝업을 예약 후 방문한 이용자인지 검증
     *
     * @param user 이용자
     */
    public void verifyReviewAuthority(User user) {
        // 해당 예약의 예약자가 아닌 경우
        if (!this.user.getId().equals(user.getId())) {
            throw new CustomException(WRONG_RESERVED_USER);
        }
        // 예약한 팝업에 방문 완료한 예약자가 아닌 경우
        if (!this.reservationStatus.equals(ReservationStatus.FINISHED)) {
            throw new CustomException(UNVISITED_USER);
        }
    }

}
