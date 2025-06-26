package com.javanc.repository;

import com.javanc.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserEntity> findById(Long id);
    Long countByRoles_id(Long role_id);
    List<UserEntity> findByRoles_id(Long role_id);
}
