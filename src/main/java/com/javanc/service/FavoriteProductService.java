package com.javanc.service;

import com.javanc.model.request.client.ProductFavoriteRequest;
import com.javanc.model.response.client.ProductFavoriteResponse;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface FavoriteProductService {
    void createFavorite(List<ProductFavoriteRequest> productFavoriteRequests);
    void updateFavorite(List<ProductFavoriteRequest> productFavoriteRequests);
    List<ProductFavoriteResponse> getFavoriteByUser();
}
