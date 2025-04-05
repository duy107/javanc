package com.javanc.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")

public class UserController {

    @GetMapping
    public ResponseEntity<?> addUser() {
        return ResponseEntity.ok().body("OK");
    }
}
