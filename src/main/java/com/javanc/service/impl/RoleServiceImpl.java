package com.javanc.service.impl;


import com.javanc.controlleradvice.customeException.AppException;
import com.javanc.enums.ErrorCode;
import com.javanc.repository.RoleRepository;
import com.javanc.repository.entity.RoleEntity;
import com.javanc.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;

    @Override
    public List<RoleEntity> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void removeAllPermissionsFromRole(Long roleId) {
        RoleEntity role = roleRepository.findById(roleId).orElseThrow(
                () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );
        role.getPermissions().clear();
        roleRepository.save(role);
    }

}
