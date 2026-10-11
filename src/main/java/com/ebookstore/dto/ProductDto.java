package com.ebookstore.dto;

import com.ebookstore.model.Product;
import java.math.BigDecimal;
import java.time.Instant;

public record ProductDto(
        Long id,
        String title,
        String author,
        String description,
        BigDecimal price,
        Integer stockQty,
        String imageUrl,
        Long categoryId,
        String categoryName,
        Long brandId,
        String brandName,
        Integer tentativeDeliveryDays,
        Instant createdAt
) {
    public static ProductDto fromEntity(Product product) {
        if (product == null) {
            return null;
        }
        Long catId = product.getCategory() != null ? product.getCategory().getId() : null;
        String catName = product.getCategory() != null ? product.getCategory().getName() : null;
        Long brId = product.getBrand() != null ? product.getBrand().getId() : null;
        String brName = product.getBrand() != null ? product.getBrand().getName() : null;

        return new ProductDto(
                product.getId(),
                product.getTitle(),
                product.getAuthor(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQty(),
                product.getImageUrl(),
                catId,
                catName,
                brId,
                brName,
                product.getTentativeDeliveryDays(),
                product.getCreatedAt()
        );
    }
}
