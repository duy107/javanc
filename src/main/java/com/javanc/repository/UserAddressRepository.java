package com.javanc.repository;

import com.javanc.repository.entity.UserAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddressEntity, Long> {
}
