package com.javanc.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanc.model.response.client.DetailClientResponse;
import com.javanc.model.response.client.DiscountClientResponse;
import com.javanc.service.ChatbotService;
import com.javanc.service.OpenAIService;
import com.javanc.service.QdrantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    OpenAIService openAIService;
    QdrantService qdrantService;
    ObjectMapper objectMapper;


    @Override
    public String answer(Map<String, Object> request) {
        String question = (String) request.get("question");
        List<Float> embedding = openAIService.createEmbedding(question);
        var results = qdrantService.search("products_collection", embedding, 10);
        StringBuilder context = new StringBuilder();
        for (var point : results) {
            Map<String, Object> payload = (Map<String, Object>) point.get("payload");

            String name = (String) payload.get("name");
            String description = (String) payload.get("description");
            Double price = (Double) payload.get("price");
            String slug = (String) payload.get("slug");
            Double percent = 0.0;
            String detail = "";
            if(payload.get("details") instanceof List){
                List<DetailClientResponse> details = ((List<?>) payload.get("details")).stream()
                        .map(item -> objectMapper.convertValue(item, DetailClientResponse.class))
                        .collect(Collectors.toList());
                detail = String.join(", ", details.stream().map(item ->
                        String.format("Size: %s, Màu sắc: %s, Còn lại: %s sản phẩm\n", item.getSize().getName(), item.getColor().getName(), item.getStock())
                ).collect(Collectors.toList()));
            }else {
                detail = payload.get("details").toString();
            }

            if (payload.get("discounts") instanceof List discountsList && !discountsList.isEmpty()) {
                List<DiscountClientResponse> discounts = ((List<?>) discountsList).stream()
                        .map(item -> objectMapper.convertValue(item, DiscountClientResponse.class))
                        .collect(Collectors.toList());

                percent = discounts.get(0).getPercent();
            }

            context.append(String.format("Sản phẩm: %s\n", name));
            context.append(String.format("- Mô tả: %s\n", description));
            context.append(String.format("- Giá: %s VNĐ\n", price.longValue()));
            context.append(String.format("- Giảm giá: %s\n", percent));
            context.append("- Chi tiết: \n");
            context.append(String.format("+ %s \n", detail));
            context.append("\n\n");
        }

        // Tạo prompt cho GPT
        String prompt = String.format(
                "Bạn là trợ lý ảo của cửa hàng. Hãy trả lời câu hỏi sau dựa trên thông tin sản phẩm:\n"
                        + "Câu hỏi: %s\n\n"
                        + "Thông tin sản phẩm liên quan:\n%s\n"
                        + "Hãy trả lời một cách thân thiện, chính xác và hữu ích.",
                question, context.toString());
        return openAIService.chatCompletion(prompt);
    }
}
