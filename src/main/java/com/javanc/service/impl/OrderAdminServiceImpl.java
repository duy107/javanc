package com.javanc.service.impl;

import com.javanc.model.response.admin.OrderAdminResponse;
import com.javanc.repository.OrderRepository;
import com.javanc.repository.ProductRepository;
import com.javanc.repository.entity.OrderEntity;
import com.javanc.service.OrderAdminService;
import com.javanc.service.WebSocketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import com.javanc.repository.entity.OrderProductEntity;
import com.javanc.repository.entity.ProductEntity;
import com.javanc.repository.DetailRepository;
import com.javanc.repository.entity.DetailEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderAdminServiceImpl implements OrderAdminService {

    OrderRepository orderRepository;
    DetailRepository detailRepository;
    ProductRepository productRepository;
    OrderWebSocketServiceImpl orderWebSocketServiceImpl;

    @Override
    public List<OrderAdminResponse> getAllOrders() {
        return orderRepository.findByDeletedFalse().stream()
                .map(order -> OrderAdminResponse.builder()
                        .id(order.getId())
                        .status(order.getStatus())
                        .time(order.getTime())
                        .total(order.getTotal())
                        .username(order.getUser().getName())
                        .payment(order.getPayment().getName())
                        .deleted(order.getDeleted())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long orderId, String status) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        order.setStatus(status);
        orderRepository.save(order);
        // Chỉ cập nhật tồn kho nếu chuyển từ "Chờ xác nhận" hoặc "Đã hủy" sang "Đang giao" hoặc "Hoàn thành"
        if (status.equals("Hoàn thành")) {

            for (OrderProductEntity orderProduct : order.getOrderProducts()) {
                Long productId = orderProduct.getProduct().getId();
                Long sizeId = orderProduct.getSize().getId();
                Long colorId = orderProduct.getColor().getId();
                Long quantityOrdered = orderProduct.getQuantity();

                // Tìm detail
                DetailEntity detail = detailRepository
                        .findByProductIdAndSizeIdAndColorId(productId, sizeId, colorId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm phù hợp."));

                if (detail.getStock() < quantityOrdered) {
                    throw new RuntimeException("Sản phẩm không đủ tồn kho.");
                }

                // Cập nhật tồn kho và số lượng bán
                detail.setStock(detail.getStock() - quantityOrdered);
                detail.setSold_count(detail.getSold_count() + quantityOrdered);
                ProductEntity productEntity = orderProduct.getProduct();
                productEntity.setQuantity(productEntity.getQuantity() - orderProduct.getQuantity());
                productRepository.save(productEntity);
                detailRepository.save(detail); // Đảm bảo bạn có repository cho DetailEntity

            }
        }

        orderWebSocketServiceImpl.sendForOnlyUser(order);
    }



    @Override
    public OrderAdminResponse getOrderById(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return OrderAdminResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .time(order.getTime())
                .total(order.getTotal())
                .deleted(order.getDeleted())
                .build();
    }
}
