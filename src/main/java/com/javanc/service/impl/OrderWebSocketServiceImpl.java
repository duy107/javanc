package com.javanc.service.impl;

import com.javanc.repository.NotificationRepository;
import com.javanc.repository.entity.NotificationEntity;
import com.javanc.repository.entity.OrderEntity;
import com.javanc.service.WebSocketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderWebSocketServiceImpl implements WebSocketService<OrderEntity> {

    SimpMessagingTemplate messagingTemplate;
    NotificationRepository notificationRepository;


    @Override
    public void sendForAllUser(OrderEntity entity) {

    }

    @Override
    public void sendForOnlyUser(OrderEntity order) {
        String message = "Đơn hàng #" + order.getId() + " đã được cập nhật trạng thái: " + order.getStatus();
        NotificationEntity notification = NotificationEntity.builder()
                .content(message)
                .user(order.getUser())
                .createdAt(LocalDateTime.now())
                .status(false)
                .build();
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/order/" + order.getUser().getId(), message);
    }
}
