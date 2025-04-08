package com.javanc.service.impl;


import com.javanc.repository.RoleRepository;
import com.javanc.repository.entity.RoleEntity;
import com.javanc.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;

    @Override
    public List<RoleEntity> listRoles() {
        return roleRepository.findAll();
    }




}
