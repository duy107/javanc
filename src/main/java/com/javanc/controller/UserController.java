package com.javanc.controller;


import com.javanc.model.response.ApiResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")

public class UserController {

    @PostMapping
    public ResponseEntity<?> addUser() {
        return ResponseEntity.ok().body(
                ApiResponseDTO.<Void>builder()
                        .code(200)
                        .message("Success")
                        .build()
        );
    }
}
