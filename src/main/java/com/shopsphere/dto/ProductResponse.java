package com.shopsphere.dto;

import com.shopsphere.entity.Product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        BigDecimal price,
        Integer stock
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(),
                p.getImageUrl(), p.getPrice(), p.getStock());
    }
}