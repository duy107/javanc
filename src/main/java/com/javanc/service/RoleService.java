package com.javanc.service;

import com.javanc.repository.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleService {
    List<RoleEntity> listRoles();
    void removeAllPermissionsFromRole(Long roleId);
}
