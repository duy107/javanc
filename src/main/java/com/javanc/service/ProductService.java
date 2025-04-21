package com.javanc.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.javanc.model.request.admin.ProductAdminRequest;

import java.io.IOException;

public interface ProductService {
    void createProduct(ProductAdminRequest productAdminRequest) throws IOException;
}
