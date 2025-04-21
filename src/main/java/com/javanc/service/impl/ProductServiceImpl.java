package com.javanc.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanc.controlleradvice.customeException.AppException;
import com.javanc.enums.ErrorCode;
import com.javanc.model.request.admin.ProductAdminRequest;
import com.javanc.model.request.common.DetailCommonResquest;
import com.javanc.repository.*;
import com.javanc.repository.entity.*;
import com.javanc.service.ProductService;
import com.javanc.service.UploadImageFileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    ObjectMapper objectMapper;
    ProductRepository productRepository;
    DetailRepository detailRepository;
    ColorRepository colorRepository;
    SizeRepository sizeRepository;
    ImageRepository imageRepository;
    UploadImageFileService uploadImageFileService;

    @Override
    public void createProduct(ProductAdminRequest productAdminRequest) throws IOException {
        List<DetailCommonResquest> variants = objectMapper.readValue(
                productAdminRequest.getVariants(),
                new TypeReference<List<DetailCommonResquest>>() {});

        // create Product
        ProductEntity newProduct = ProductEntity.builder()
                .name(productAdminRequest.getName())
                .slug(productAdminRequest.getSlug())
                .price(productAdminRequest.getPrice())
                .quantity(productAdminRequest.getQuantity())
                .description(productAdminRequest.getDescription())
                .build();
        productRepository.save(newProduct);

        // create detail
        for(DetailCommonResquest variant : variants) {
            ColorEntity color = colorRepository.findById(variant.getColorId()).orElseThrow(
                    () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
            );
            SizeEntity size = sizeRepository.findById(variant.getSizeId()).orElseThrow(
                    () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
            );

            DetailEntity detail = DetailEntity.builder()
                    .product(newProduct)
                    .color(color)
                    .size(size)
                    .stock(variant.getStock())
                    .build();
            detailRepository.save(detail);
        }
        // create image
        for(MultipartFile avatar : productAdminRequest.getImages()) {
            String src = uploadImageFileService.uploadImage(avatar);
            ImageEntity image = ImageEntity.builder()
                    .src(src)
                    .product(newProduct)
                    .build();
            imageRepository.save(image);
        }
    }
}
