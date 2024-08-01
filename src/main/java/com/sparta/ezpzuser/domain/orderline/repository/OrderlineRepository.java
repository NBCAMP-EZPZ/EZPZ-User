package com.sparta.ezpzuser.domain.orderline.repository;

import com.sparta.ezpzuser.domain.orderline.entity.Orderline;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderlineRepository extends JpaRepository<Orderline, Long> {

    List<Orderline> findAllByOrderId(Long orderId);
}
