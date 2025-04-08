package com.javanc.controller;


import com.javanc.model.request.AuthenRequest;
import com.javanc.model.response.ApiResponseDTO;
import com.javanc.model.response.AuthenResponse;
import com.javanc.service.AuthenService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationController {

    AuthenService authenService;

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody AuthenRequest authenRequest){
        AuthenResponse response = authenService.login(authenRequest);
        return ResponseEntity.ok().body(
                ApiResponseDTO.<AuthenResponse>builder()
                        .result(response)
                        .build()
        );
    }

    @PostMapping("/introspect")
    public ResponseEntity<?> introspect (@RequestBody AuthenRequest authenRequest) throws JOSEException, ParseException {
        AuthenResponse response = authenService.introspect(authenRequest);
        return ResponseEntity.ok().body(
                ApiResponseDTO.<AuthenResponse>builder()
                        .result(response)
                        .build()
        );
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthenRequest authenRequest){
        return ResponseEntity.ok().body(
                ApiResponseDTO.<AuthenResponse>builder()
                        .result(authenService.register(authenRequest))
                        .build()
        );
    }
}
