package com.javanc.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @PostMapping("/login")
    public ResponseEntity<?> login (){
        return ResponseEntity.ok().body("OK");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(){
        return ResponseEntity.ok().body("OK");
    }
}
