package com.javanc.controller;

import com.javanc.model.response.ApiResponseDTO;
import com.javanc.repository.entity.RoleEntity;
import com.javanc.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor

public class RoleController {

    RoleService roleService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok().body(
                ApiResponseDTO.<List<RoleEntity>>builder()
                        .result(roleService.listRoles())
                        .build()
        );
    }
}
