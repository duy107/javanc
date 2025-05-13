package com.javanc.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanc.controlleradvice.customeException.AppException;
import com.javanc.enums.ErrorCode;
import com.javanc.model.request.admin.ProductAdminRequest;
import com.javanc.model.request.common.DetailCommonResquest;
import com.javanc.model.response.admin.DetailResponse;
import com.javanc.model.response.admin.ProductAdminResponse;
import com.javanc.model.response.admin.ProductPaginationResponse;
import com.javanc.repository.*;
import com.javanc.repository.custom.FilterProductCustom;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    CategoryRepository categoryRepository;
    ImageRepository imageRepository;
    UploadImageFileService uploadImageFileService;
    FilterProductCustom filterProductCustom;

    @Override
    public void createProduct(ProductAdminRequest productAdminRequest) throws IOException {
        List<DetailCommonResquest> variants = objectMapper.readValue(
                productAdminRequest.getVariants(),
                new TypeReference<List<DetailCommonResquest>>() {
                });

        CategoryEntity category = categoryRepository.findById(productAdminRequest.getCategoryId()).orElseThrow(
                () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );

        // create Product
        ProductEntity product = null;
        if(productAdminRequest.getId() == null) {
            product = ProductEntity.builder()
                    .name(productAdminRequest.getName())
                    .slug(productAdminRequest.getSlug())
                    .price(productAdminRequest.getPrice())
                    .quantity(productAdminRequest.getQuantity())
                    .description(productAdminRequest.getDescription())
                    .category(category)
                    .build();
        }else {
            product = productRepository.findById(productAdminRequest.getId()).orElseThrow(
                    () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
            product.getImages().clear();
            product.getDetails().clear();
            product.setName(productAdminRequest.getName());
            product.setSlug(productAdminRequest.getSlug());
            product.setPrice(productAdminRequest.getPrice());
            product.setQuantity(productAdminRequest.getQuantity());
            product.setDescription(productAdminRequest.getDescription());
            product.setCategory(category);
        }
        productRepository.save(product);

        // create detail
        for (DetailCommonResquest variant : variants) {
            ColorEntity color = colorRepository.findById(variant.getColorId()).orElseThrow(
                    () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
            );
            SizeEntity size = sizeRepository.findById(variant.getSizeId()).orElseThrow(
                    () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
            );

            DetailEntity detail = DetailEntity.builder()
                    .product(product)
                    .color(color)
                    .size(size)
                    .stock(variant.getStock())
                    .build();
            detailRepository.save(detail);
        }
        // create image
        for (MultipartFile avatar : productAdminRequest.getImages()) {
            String src = uploadImageFileService.uploadImage(avatar);
            ImageEntity image = ImageEntity.builder()
                    .src(src)
                    .product(product)
                    .build();
            imageRepository.save(image);
        }
    }

    @Override
    public ProductPaginationResponse getProducts(String searchKey, String categoryId, String status, String pageNumber) {
        List<ProductAdminResponse> result = new ArrayList<>();
        Map<String, Object> listProducts = filterProductCustom.findProductByOption(searchKey, categoryId, status, pageNumber);

        @SuppressWarnings("unchecked")
        List<ProductEntity> productEntities = (List<ProductEntity>) listProducts.get("products");
        Long total = (Long) listProducts.get("totalProducts");
        Long limitProduct = (Long) listProducts.get("limitProduct");

        for (ProductEntity productEntity : productEntities) {
            List<ImageEntity> listImages = imageRepository.findByProduct_Id(productEntity.getId());
            List<String> listSrc = listImages.isEmpty() ? new ArrayList<>() : listImages.stream().map(ImageEntity::getSrc).collect(Collectors.toList());
            ProductAdminResponse productAdminResponse = ProductAdminResponse.builder()
                    .id(productEntity.getId())
                    .name(productEntity.getName())
                    .slug(productEntity.getSlug())
                    .deleted(productEntity.getDeleted())
                    .price(productEntity.getPrice())
                    .quantity(productEntity.getQuantity())
                    .category(productEntity.getCategory())
                    .src(listSrc)
                    .build();
            result.add(productAdminResponse);
        }
        return ProductPaginationResponse.builder()
                .products(result)
                .totalProducts(total)
                .limitProduct(limitProduct)
                .build();
    }

    @Override
    public void deleteProducts(List<Long> ids) {
        List<ProductEntity> listProducts = productRepository.findAllById(ids);
        for(ProductEntity productEntity : listProducts){
            productEntity.setDeleted(!productEntity.getDeleted()    );

        }
        productRepository.saveAll(listProducts);
    }

    @Override
    public ProductAdminResponse getProductById(Long id) {
        List<String> src = new ArrayList<>();
        List<DetailResponse> variants = new ArrayList<>();

        ProductEntity product = productRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );
        List<DetailEntity> details = detailRepository.findAllByProduct_id(id);
        List<ImageEntity> images = imageRepository.findByProduct_Id(id);
        if(!details.isEmpty()){
            for(DetailEntity detailEntity : details){
                DetailResponse detailResponse = DetailResponse.builder()
                        .colorId(detailEntity.getColor().getId())
                        .sizeId(detailEntity.getSize().getId())
                        .stock(detailEntity.getStock())
                        .build();
                variants.add(detailResponse);
            }
        }

        if(!images.isEmpty()){
            for(ImageEntity imageEntity : images){
                src.add(imageEntity.getSrc());
            }
        }
        return ProductAdminResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .price(product.getPrice())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .category(product.getCategory())
                .src(src)
                .variants(variants)
                .build();
    }
}
