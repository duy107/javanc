package com.javanc.repository;

import com.javanc.repository.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByCategory_IdAndSlugAndDeleted(Long categoryId, String slug, Boolean deleted);
    void deleteAllByIdIn(List<Long> ids);
    List<ProductEntity> findAllByIdIn(List<Long> ids);
}
