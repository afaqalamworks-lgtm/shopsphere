package com.shopsphere.service;

import com.shopsphere.dto.ProductRequest;
import com.shopsphere.dto.ProductResponse;
import com.shopsphere.entity.Product;
import com.shopsphere.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> list(String q, Pageable pageable) {
        Page<Product> page = (q == null || q.isBlank())
                ? productRepository.findAll(pageable)
                : productRepository.findByNameContainingIgnoreCase(q.trim(), pageable);
        return page.map(ProductResponse::from);
    }

    public ProductResponse get(Long id) {
        return ProductResponse.from(findOrThrow(id));
    }

    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .price(request.price())
                .stock(request.stock())
                .build();
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findOrThrow(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setImageUrl(request.imageUrl());
        product.setPrice(request.price());
        product.setStock(request.stock());
        return ProductResponse.from(product);
    }

    public void delete(Long id) {
        Product product = findOrThrow(id);
        try {
            productRepository.delete(product);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This product is part of existing orders and cannot be deleted");
        }
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));
    }
}