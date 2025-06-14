package com.javanc.service.impl;

import com.javanc.repository.ColorRepository;
import com.javanc.repository.entity.ColorEntity;
import com.javanc.service.ColorService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ColorServiceImpl implements ColorService {

    ColorRepository repository;

    @Override
    public List<ColorEntity> getColors() {
        return repository.findAll();
    }
}
