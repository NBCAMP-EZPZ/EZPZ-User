package com.sparta.ezpzuser.domain.order.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.domain.order.enums.OrderStatus;
import com.sparta.ezpzuser.domain.orderline.entity.Orderline;
import com.sparta.ezpzuser.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(nullable = false)
    private int totalPrice;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Orderline> orderlineList = new ArrayList<>();

    /**
     * Order 생성자
     *
     * @param user 주문자
     */
    private Order(User user) {
        this.user = user;
    }

    /**
     * Order 정적 팩토리 메서드
     *
     * @param user 주문자
     * @return
     */
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
}
