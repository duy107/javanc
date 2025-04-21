package com.javanc.repository;

import com.javanc.repository.entity.DetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailRepository extends JpaRepository<DetailEntity, Long> {
}
