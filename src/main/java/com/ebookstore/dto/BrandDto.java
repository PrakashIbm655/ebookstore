package com.ebookstore.dto;

import com.ebookstore.model.Brand;

public record BrandDto(
        Long id,
        String name,
        String logoUrl
) {
    public static BrandDto fromEntity(Brand brand) {
        if (brand == null) {
            return null;
        }
        return new BrandDto(
                brand.getId(),
                brand.getName(),
                brand.getLogoUrl()
        );
    }
}
