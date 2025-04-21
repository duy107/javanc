package com.javanc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javanc.model.request.AuthenRequest;
import com.javanc.model.request.auth.RegisterRequest;
import com.javanc.model.response.ApiResponseDTO;
import com.javanc.model.response.AuthenResponse;
import com.javanc.repository.UserRepository;
import com.javanc.service.AuthenService;
import com.javanc.service.EmailService;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    AuthenService authenService;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    EmailService emailService;

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

    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(@Valid @ModelAttribute RegisterRequest registerRequest, BindingResult result) throws IOException, MessagingException {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors() // lấy các field lỗi
                    .stream().map(FieldError::getDefaultMessage) // lấy message của từng field bị lỗi
                    .collect(Collectors.toList());
            ApiResponseDTO<List<String>> apiResponseDTO = ApiResponseDTO.<List<String>>builder()
                    .code(400)
                    .result(errorMessages)
                    .build();
            return ResponseEntity.badRequest().body(apiResponseDTO);
        }
        authenService.sendEmail(registerRequest);
        return ResponseEntity.ok().body(ApiResponseDTO.<String>builder()
                .message("Vui lòng kiểm tra email")
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthenRequest authenRequest) throws JsonProcessingException {
        authenService.register(authenRequest);
        return ResponseEntity.ok().body(
                ApiResponseDTO.<Void>builder()
                        .message("Ok")
                        .build()
        );
    }
}
