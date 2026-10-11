package com.ebookstore.service;

import com.ebookstore.dto.BrandDto;
import com.ebookstore.dto.CategoryDto;
import com.ebookstore.dto.ProductDto;
import com.ebookstore.exception.ResourceNotFoundException;
import com.ebookstore.model.Category;
import com.ebookstore.model.Product;
import com.ebookstore.repository.BrandRepository;
import com.ebookstore.repository.CategoryRepository;
import com.ebookstore.repository.ProductRepository;
import com.ebookstore.repository.ProductSpecification;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CatalogueService {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public CatalogueService(CategoryRepository categoryRepository,
                            BrandRepository brandRepository,
                            ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryDto::fromEntity)
                .toList();
    }

    public List<BrandDto> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(BrandDto::fromEntity)
                .toList();
    }

    public Page<ProductDto> getProducts(Long categoryId, Long brandId,
                                        BigDecimal minPrice, BigDecimal maxPrice,
                                        String keyword, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.filterBy(
                categoryId, brandId, minPrice, maxPrice, keyword);
        return productRepository.findAll(spec, pageable)
                .map(ProductDto::fromEntity);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductDto.fromEntity(product);
    }

    public Page<ProductDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        return productRepository.findByCategoryId(category.getId(), pageable)
                .map(ProductDto::fromEntity);
    }
}
