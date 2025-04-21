package com.javanc.service;

import com.javanc.model.request.admin.CategoryAdminRequest;
import com.javanc.model.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> listCategories();
    void createCategory(CategoryAdminRequest request);
    CategoryResponse getCategory(Long id);
    void updateCategory(Long id, CategoryAdminRequest request);
}
