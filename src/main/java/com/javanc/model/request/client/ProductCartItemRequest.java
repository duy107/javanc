package com.javanc.model.request.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCartItemRequest {
    Long id;
    Long quantity;
    String name;
    Double percent;
    float price;
    Long stock;
    ColorRequest color;
    SizeRequest size;
    ImageRequest image;
}
