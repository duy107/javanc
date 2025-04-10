package com.javanc.controller.admin;

import com.javanc.model.response.ApiResponseDTO;
import com.javanc.repository.entity.RoleEntity;
import com.javanc.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/roles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class RoleAdminController {

    RoleService roleService;

    @GetMapping
    @PostAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ROLE_MANAGEMENT')")
    public ResponseEntity<?> getRoles(){
//        Authentication securityContextHolder = SecurityContextHolder.getContext().getAuthentication();
//        log.info("User name: " + securityContextHolder.getName());
//        securityContextHolder.getAuthorities().forEach(grantedAuthority -> log.info("GrantedAuthority: {}", grantedAuthority));
        return ResponseEntity.ok().body(
                ApiResponseDTO.<List<RoleEntity>>builder()
                        .result(roleService.listRoles())
                        .build()
        );
    }
}
