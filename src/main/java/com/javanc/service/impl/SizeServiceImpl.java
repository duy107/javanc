package com.javanc.service.impl;

import com.javanc.repository.SizeRepository;
import com.javanc.repository.entity.SizeEntity;
import com.javanc.service.SizeService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class SizeServiceImpl implements SizeService {

    SizeRepository sizeRepository;

    @Override
    public List<SizeEntity> getSizes() {
        return sizeRepository.findAll();
    }
}
