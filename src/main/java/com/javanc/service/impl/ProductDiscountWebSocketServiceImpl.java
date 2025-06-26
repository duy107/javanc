package com.javanc.service.impl;

import com.javanc.repository.NotificationRepository;
import com.javanc.repository.UserRepository;
import com.javanc.repository.entity.NotificationEntity;
import com.javanc.repository.entity.ProductDiscountEntity;
import com.javanc.repository.entity.UserEntity;
import com.javanc.service.WebSocketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductDiscountWebSocketServiceImpl implements WebSocketService<ProductDiscountEntity> {

    SimpMessagingTemplate messagingTemplate;
    UserRepository userRepository;
    NotificationRepository notificationRepository;


    @Override
    public void sendForOnlyUser(ProductDiscountEntity entity) {

    }

    @Override
    public void sendForAllUser(ProductDiscountEntity entity) {
        String message = String.format("Sản phẩm %s đang có khuyến mại %s%%", entity.getProduct().getName(), entity.getDiscount().getPercent());
        List<UserEntity> users = userRepository.findByRoles_id(2L);
        for (UserEntity user : users) {
            NotificationEntity notification = NotificationEntity.builder()
                    .content(message)
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .status(false)
                    .build();
            notificationRepository.save(notification);
        }
        messagingTemplate.convertAndSend("/topic/discount", message);
    }
}
