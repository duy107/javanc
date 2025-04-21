package com.javanc.controller;

import com.javanc.model.response.ApiResponseDTO;
import com.javanc.repository.entity.ColorEntity;
import com.javanc.repository.entity.SizeEntity;
import com.javanc.service.ColorService;
import com.javanc.service.SizeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/common")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CommonController {

    ColorService colorService;
    SizeService sizeService;

    @GetMapping("/colors")
    public ResponseEntity<?> getColors() {
        return ResponseEntity.ok().body(
                ApiResponseDTO.<List<ColorEntity>>builder()
                        .result(colorService.getColors())
                        .build()
        );
    }

    @GetMapping("/sizes")
    public ResponseEntity<?> getSizes() {
        return ResponseEntity.ok().body(
                ApiResponseDTO.<List<SizeEntity>>builder()
                        .result(sizeService.getSizes())
                        .build()
        );
    }

}
