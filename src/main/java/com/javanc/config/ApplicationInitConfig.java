package com.javanc.config;

import com.javanc.controlleradvice.customeException.AppException;
import com.javanc.enums.ErrorCode;
import com.javanc.repository.RoleRepository;
import com.javanc.repository.UserRepository;
import com.javanc.repository.entity.RoleEntity;
import com.javanc.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ApplicationInitConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner init(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            if(userRepository.findByEmail("admin@admin.com").isEmpty()){
                RoleEntity roleEntity = roleRepository.findByCode("ADMIN").orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
                UserEntity userEntity = UserEntity.builder()
                        .email("admin@admin.com")
                        .password(passwordEncoder.encode("12345"))
                        .roles(List.of(roleEntity))
                        .build();
                userRepository.save(userEntity);
                log.info("admin user created with by default password 12345");
            }
        };
    }
}
