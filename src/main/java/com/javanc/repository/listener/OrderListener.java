package com.javanc.repository.listener;

import com.javanc.repository.entity.OrderEntity;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderListener implements IListener<OrderEntity> {

    @Override
    @PostUpdate
    public void afterUpdate(OrderEntity entity) {
        log.info("order id: " + entity.getId());
        log.info("user name: " + entity.getUser().getName());
    }

    @Override
    @PreUpdate
    public void beforeUpdate(OrderEntity entity) {

    }
}
