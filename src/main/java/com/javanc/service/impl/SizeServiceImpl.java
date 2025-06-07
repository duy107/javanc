package com.javanc.service.impl;

<<<<<<< HEAD
=======
import com.javanc.model.response.common.SizeResponse;
>>>>>>> implement_chatbot
import com.javanc.repository.SizeRepository;
import com.javanc.repository.entity.SizeEntity;
import com.javanc.service.SizeService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
<<<<<<< HEAD
=======
import java.util.stream.Collectors;
>>>>>>> implement_chatbot

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class SizeServiceImpl implements SizeService {

    SizeRepository sizeRepository;

    @Override
<<<<<<< HEAD
    public List<SizeEntity> getSizes() {
        return sizeRepository.findAll();
=======
    public List<SizeResponse> getSizes() {
        List<SizeEntity> listSize = sizeRepository.findAll();
        return listSize.stream().map(item ->
                SizeResponse.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .build()
        ).collect(Collectors.toList());
>>>>>>> implement_chatbot
    }
}
