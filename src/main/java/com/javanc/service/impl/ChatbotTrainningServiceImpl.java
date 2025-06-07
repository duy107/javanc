package com.javanc.service.impl;

import com.javanc.model.response.client.*;
import com.javanc.repository.ProductRepository;
import com.javanc.repository.entity.ProductEntity;
import com.javanc.service.ChatbotTrainningService;
import com.javanc.service.OpenAIService;
import com.javanc.service.QdrantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ChatbotTrainningServiceImpl implements ChatbotTrainningService {

    ProductServiceImpl productServiceImpl;
    QdrantService qdrantService;
    OpenAIService openAIService;
    ProductRepository productRepository;

    @Override
    public void trainningWithProduct() {

        List<ProductClientResponse> products = productRepository.findAll()
                .stream().map(productServiceImpl::mapToProductClientResponse)
                .collect(Collectors.toList());
        for(ProductClientResponse item : products) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("productId", item.getId());
            payload.put("name", item.getName());
            payload.put("price", item.getPrice());
            payload.put("description", item.getDescription());
            payload.put("slug", item.getSlug());
            payload.put("quantity", item.getQuantity());
            payload.put("category", item.getCategory());
            payload.put("images", item.getImages());
            payload.put("details", item.getDetails());
            payload.put("discounts", item.getDiscounts());
            payload.put("feedbacks", item.getFeedbacks());

            String productInfor = buildInforProductString(item);
            List<Float> embedding = openAIService.createEmbedding(productInfor);
            Long pointId = item.getId();
            qdrantService.upsertPoint("products_collection", pointId, embedding, payload);

        }
    }

    private String buildInforProductString(ProductClientResponse product) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        sb.append("Tên sản phẩm: ").append(product.getName()).append(". ");
        sb.append("Mô tả: ").append(product.getDescription()).append(". ");
        sb.append("Giá: ").append(product.getPrice()).append(" VND. ");
        sb.append("Số lượng: ").append(product.getQuantity()).append(". ");

        if (product.getCategory() != null) {
            sb.append("Danh mục: ").append(product.getCategory().getName()).append(". ");
        }

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            sb.append("Hình ảnh: ");
            for (ImageClientResponse img : product.getImages()) {
                sb.append(img.getSrc()).append(", ");
            }
            sb.setLength(sb.length() - 2); // bỏ dấu ", " cuối cùng
            sb.append(". ");
        }

        if (product.getDetails() != null && !product.getDetails().isEmpty()) {
            sb.append("Chi tiết sản phẩm: ");
            for (DetailClientResponse detail : product.getDetails()) {
                sb.append("Size: ").append(detail.getSize().getName())
                        .append(", Màu: ").append(detail.getColor().getName())
                        .append(", Tồn kho: ").append(detail.getStock())
                        .append(", Đã bán: ").append(detail.getSoldCount())
                        .append(" | ");
            }
            sb.setLength(sb.length() - 3); // bỏ dấu " | " cuối cùng
            sb.append(". ");
        }

        if (product.getDiscounts() != null && !product.getDiscounts().isEmpty()) {
            sb.append("Khuyến mãi: ");
            for (DiscountClientResponse discount : product.getDiscounts()) {
                sb.append("Giảm ").append(discount.getPercent()).append("%")
                        .append(" từ ").append(sdf.format(discount.getStartDate()))
                        .append(" đến ").append(sdf.format(discount.getEndDate()))
                        .append(". ");
            }
        }

        if (product.getFeedbacks() != null && !product.getFeedbacks().isEmpty()) {
            sb.append("Đánh giá sản phẩm: ");
            for (FeedbackResponse fb : product.getFeedbacks()) {
                sb.append("Người dùng ").append(fb.getName())
                        .append(" (").append(sdf.format(fb.getTime())).append("), điểm đánh giá: ")
                        .append(fb.getRating())
                        .append(", nhận xét: ").append(fb.getDescription())
                        .append(". ");
            }
        }

        return sb.toString();
    }


}
