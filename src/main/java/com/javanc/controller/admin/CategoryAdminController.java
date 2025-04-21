package com.javanc.controller.admin;

import com.javanc.model.request.admin.CategoryAdminRequest;
import com.javanc.model.response.ApiResponseDTO;
import com.javanc.model.response.CategoryResponse;
import com.javanc.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryAdminController {

    CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok().body(
                ApiResponseDTO.<List<CategoryResponse>>builder()
                        .result(categoryService.listCategories())
                        .build()
        );
    }
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryAdminRequest categoryAdminRequest) {
        categoryService.createCategory(categoryAdminRequest);
        return ResponseEntity.ok().body(
                ApiResponseDTO.<Void>builder()
                        .message("Tạo danh mục thành công")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok().body(
                ApiResponseDTO.<CategoryResponse>builder()
                        .result(categoryService.getCategory(id))
                        .build()
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryAdminRequest categoryAdminRequest) {
        categoryService.updateCategory(id, categoryAdminRequest);
        return ResponseEntity.ok().body(ApiResponseDTO.<Void>builder().message("Ok").build());
    }
}
