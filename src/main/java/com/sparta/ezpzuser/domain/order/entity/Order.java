package com.sparta.ezpzuser.domain.order.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.domain.order.enums.OrderStatus;
import com.sparta.ezpzuser.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.sparta.ezpzuser.common.exception.ErrorType.ORDER_CANCELLATION_NOT_ALLOWED;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "orders")
public class Order extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    private int totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Orderline> orderlineList = new ArrayList<>();

    /**
     * 연관관계 편의 메서드
     */
    public void addOrderline(Orderline orderline) {
        this.orderlineList.add(orderline);
        orderline.setOrderTo(this);
    }

    /**
     * 생성자
     */
    private Order(User user, List<Orderline> orderlineList) {
        this.user = user;
        this.orderStatus = OrderStatus.ORDER_COMPLETED;
        for (Orderline orderline : orderlineList) {
            this.addOrderline(orderline);
            this.totalPrice += orderline.getOrderPrice();
        }
    }

    public static Order of(User user, List<Orderline> orderlineList) {
        return new Order(user, orderlineList);
    }

    /**
     * 주문 취소
     */
    public void cancel() {
        // 배송 전 상태인 주문만 취소 가능
        if (!this.orderStatus.equals(OrderStatus.ORDER_COMPLETED)) {
            throw new CustomException(ORDER_CANCELLATION_NOT_ALLOWED);
        }
        this.orderStatus = OrderStatus.CANCELLED;
        for (Orderline orderline : this.orderlineList) {
            orderline.cancel();
        }
    }

    // 테스트 전용 메서드
    public void startDelivery() {
        this.orderStatus = OrderStatus.IN_TRANSIT;
    }

}
