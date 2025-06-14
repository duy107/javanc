package com.javanc.repository;

import com.javanc.repository.entity.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrderRepository extends JpaRepository<OrderProductEntity, Long> {
}
