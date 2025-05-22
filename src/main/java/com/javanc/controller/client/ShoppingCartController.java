package com.javanc.controller.client;

import com.cloudinary.Api;
import com.javanc.model.request.client.ProductCartItemRequest;
import com.javanc.model.response.ApiResponseDTO;
import com.javanc.service.ShoppingCartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-cart")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ShoppingCartController {

    ShoppingCartService shoppingCartService;

    @PostMapping
    public ResponseEntity<?> addOrUpdateCart(@RequestBody List<ProductCartItemRequest> productCartItemRequests) {
        shoppingCartService.createCart(productCartItemRequests);
        return ResponseEntity.ok().body(
                ApiResponseDTO.<Void>builder()
                        .message("Add cart item success")
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<?> getCart() {
        return ResponseEntity.ok().body(
                ApiResponseDTO.<List<ProductCartItemRequest>>builder()
                        .result(shoppingCartService.getCartByUser())
                        .build()
        );
    }

    @PatchMapping
    public ResponseEntity<?> updateCart(@RequestBody List<ProductCartItemRequest> productCartItemRequests) {
        shoppingCartService.updateCart(productCartItemRequests);
        return ResponseEntity.ok().body(
                ApiResponseDTO.<Void>builder()
                        .message("Update cart item success")
                        .build()
        );
    }
}
