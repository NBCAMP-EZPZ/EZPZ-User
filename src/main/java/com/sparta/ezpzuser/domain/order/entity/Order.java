package com.sparta.ezpzuser.domain.order.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.domain.order.enums.OrderStatus;
import com.sparta.ezpzuser.domain.orderline.entity.Orderline;
import com.sparta.ezpzuser.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
     * Order 생성자
     *
     * @param user 주문자
     */
    private Order(User user) {
        this.user = user;
        this.orderStatus = OrderStatus.ORDER_COMPLETED;
    }

    public static Order of(User user) {
        return new Order(user);
    }


    /**
     * orderline 추가
     *
     * @param orderline 추가할 orderline
     */
    public void addOrderline(Orderline orderline) {
        orderlineList.add(orderline);
        calculateTotalPrice();
    }


    /**
     * 이 주문라인의 총 금액을 계산하여 반환, 엔티티가 새로 생성되거나 업데이트될 때 호출됨
     */
    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        this.totalPrice = orderlineList.stream()
                .mapToInt(orderline -> orderline.getItem().getPrice() * orderline.getQuantity())
                .sum();
    }

    /**
     * 주문 상태 변경 메서드
     *
     * @param orderStatus 변경할 상태
     */
    public void updateStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
