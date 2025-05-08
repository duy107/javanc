package com.javanc.repository;

import com.javanc.repository.entity.DetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailRepository extends JpaRepository<DetailEntity, Long> {
    List<DetailEntity> findAllByProduct_id(Long id);
}
