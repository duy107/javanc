package com.javanc.repository;

import com.javanc.repository.entity.InvalidatedTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedTokenEntity, String> {
}
