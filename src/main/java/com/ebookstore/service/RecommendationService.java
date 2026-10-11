package com.ebookstore.service;

import com.ebookstore.dto.ProductDto;
import java.util.List;

/**
 * Encapsulates the recommendation engine.
 *
 * <p>Two strategies are supported:
 * <ol>
 *   <li>Rule-based fallback — same-category products, fully implemented.</li>
 *   <li>Collaborative filtering (item-based) — stubbed until the Order module
 *       (Sub-Task 7) is available.</li>
 * </ol>
 */
public interface RecommendationService {

    /**
     * Returns up to {@code limit} products related to the given product.
     * Tries collaborative filtering first; falls back to same-category products
     * when CF yields no results (cold-start or stub).
     *
     * @param productId the reference product
     * @param limit     maximum number of results to return
     * @return list of related products (never null)
     * @throws com.ebookstore.exception.ResourceNotFoundException if the product does not exist
     */
    List<ProductDto> getRelatedProducts(Long productId, int limit);

    /**
     * Returns up to {@code limit} product recommendations for the user's cart,
     * derived from the user's order history.
     *
     * <p>Currently a stub — will be wired after Sub-Task 7 (Order module) is complete.
     *
     * @param userId the authenticated user
     * @param limit  maximum number of results to return
     * @return list of recommended products (never null)
     */
    List<ProductDto> getCartRecommendations(Long userId, int limit);
}
