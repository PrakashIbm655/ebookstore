package com.ebookstore.controller;

import com.ebookstore.dto.BrandDto;
import com.ebookstore.dto.CategoryDto;
import com.ebookstore.dto.ProductDto;
import com.ebookstore.service.CatalogueService;
import com.ebookstore.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Catalogue", description = "Product, category, and brand browsing endpoints")
public class CatalogueController {

    private final CatalogueService catalogueService;
    private final RecommendationService recommendationService;

    public CatalogueController(CatalogueService catalogueService,
                               RecommendationService recommendationService) {
        this.catalogueService = catalogueService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/categories")
    @Operation(summary = "List all product categories")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(catalogueService.getAllCategories());
    }

    @GetMapping("/categories/{id}/products")
    @Operation(summary = "List products in a category with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Page<ProductDto>> getProductsByCategory(
            @PathVariable Long id,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(catalogueService.getProductsByCategory(id, pageable));
    }

    @GetMapping("/brands")
    @Operation(summary = "List all product brands / publishers")
    @ApiResponse(responseCode = "200", description = "Brands retrieved successfully")
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        return ResponseEntity.ok(catalogueService.getAllBrands());
    }

    @GetMapping("/products")
    @Operation(summary = "Search and filter products with pagination")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    public ResponseEntity<Page<ProductDto>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String keyword,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(catalogueService.getProducts(categoryId, brandId, minPrice, maxPrice, keyword, pageable));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Get product details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogueService.getProductById(id));
    }

    /**
     * Returns up to {@code limit} products related to the given product.
     * Uses collaborative filtering with a same-category fallback.
     * This endpoint is public — no authentication required.
     */
    @GetMapping("/products/{id}/related")
    @Operation(summary = "Get related products based on collaborative filtering or category fallback")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Related products retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<List<ProductDto>> getRelatedProducts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(recommendationService.getRelatedProducts(id, limit));
    }
}
