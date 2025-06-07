package com.javanc.service;

import com.javanc.model.request.client.ProductCartItemRequest;
<<<<<<< HEAD
import com.javanc.model.response.client.ProductFavoriteResponse;
=======
>>>>>>> implement_chatbot

import java.util.List;

public interface ShoppingCartService {
    void createCart(List<ProductCartItemRequest> productCartItemRequests);
    void updateCart(List<ProductCartItemRequest> productCartItemRequests);
    List<ProductCartItemRequest> getCartByUser();
}
