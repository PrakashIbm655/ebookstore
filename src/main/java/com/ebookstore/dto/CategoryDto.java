package com.ebookstore.dto;

import com.ebookstore.model.Category;

public record CategoryDto(
        Long id,
        String name,
        String description,
        String imageUrl
) {
    public static CategoryDto fromEntity(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImageUrl()
        );
    }
}
