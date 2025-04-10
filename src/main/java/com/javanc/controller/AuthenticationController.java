package com.javanc.controller;


import com.javanc.controlleradvice.customeException.AppException;
import com.javanc.enums.ErrorCode;
import com.javanc.model.request.AuthenRequest;
import com.javanc.model.response.ApiResponseDTO;
import com.javanc.model.response.AuthenResponse;
import com.javanc.repository.UserRepository;
import com.javanc.repository.entity.UserEntity;
import com.javanc.service.AuthenService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    AuthenService authenService;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenRequest authenRequest) {
        AuthenResponse response = authenService.login(authenRequest);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("ROLE: " + auth.getAuthorities().toString());
        return ResponseEntity.ok().body(
                ApiResponseDTO.<AuthenResponse>builder()
                        .result(response)
                        .build()
        );
    }

    @PostMapping("/introspect")
    public ResponseEntity<?> introspect(@RequestBody AuthenRequest authenRequest) throws JOSEException, ParseException {
        AuthenResponse response = authenService.introspect(authenRequest);
        return ResponseEntity.ok().body(
                ApiResponseDTO.<AuthenResponse>builder()
                        .result(response)
                        .build()
        );
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody AuthenRequest authenRequest) throws ParseException, JOSEException {
        return ResponseEntity.ok().body(
                ApiResponseDTO.<AuthenResponse>builder()
                        .result(authenService.refreshToken(authenRequest))
                        .build()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthenRequest authenRequest) {
        if (userRepository.existsByEmail(authenRequest.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        authenRequest.setPassword(passwordEncoder.encode(authenRequest.getPassword()));
        userRepository.save(UserEntity.builder()
                .email(authenRequest.getEmail())
                .password(authenRequest.getPassword())
                .build()
        );
        return ResponseEntity.ok().body("Ok");
    }
}
