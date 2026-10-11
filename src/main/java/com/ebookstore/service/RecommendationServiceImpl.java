package com.ebookstore.service;

import com.ebookstore.dto.ProductDto;
import com.ebookstore.exception.ResourceNotFoundException;
import com.ebookstore.model.OrderItem;
import com.ebookstore.model.Product;
import com.ebookstore.repository.OrderItemRepository;
import com.ebookstore.repository.ProductRepository;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RecommendationServiceImpl implements RecommendationService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public RecommendationServiceImpl(ProductRepository productRepository,
                                     OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Returns up to {@code limit} products related to the given product.
     *
     * <p>Strategy:
     * <ol>
     *   <li>Try collaborative filtering: find other users who bought the same product and
     *       surface the most frequently co-purchased products.</li>
     *   <li>If CF yields no results, fall back to same-category products ordered by
     *       {@code createdAt} descending, excluding the reference product itself.</li>
     * </ol>
     */
    @Override
    public List<ProductDto> getRelatedProducts(Long productId, int limit) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // 1. Collaborative filtering: users who also bought this product
        List<OrderItem> coOrders = orderItemRepository.findByProductId(productId);
        if (!coOrders.isEmpty()) {
            // Collect the user IDs of those buyers
            Set<Long> buyerUserIds = coOrders.stream()
                    .map(oi -> oi.getOrder().getUser().getId())
                    .collect(Collectors.toSet());

            // Gather all order items from those users, excluding the reference product
            Map<Long, Long> productFrequency = new HashMap<>();
            for (Long uid : buyerUserIds) {
                orderItemRepository.findByOrderUserId(uid).stream()
                        .filter(oi -> !oi.getProduct().getId().equals(productId))
                        .forEach(oi -> productFrequency.merge(oi.getProduct().getId(), 1L, Long::sum));
            }

            if (!productFrequency.isEmpty()) {
                List<Long> topProductIds = productFrequency.entrySet().stream()
                        .sorted(Map.Entry.<Long, Long>comparingByValue(Comparator.reverseOrder()))
                        .limit(limit)
                        .map(Map.Entry::getKey)
                        .toList();

                List<ProductDto> cfResults = topProductIds.stream()
                        .map(id -> productRepository.findById(id))
                        .filter(opt -> opt.isPresent())
                        .map(opt -> ProductDto.fromEntity(opt.get()))
                        .toList();

                if (!cfResults.isEmpty()) {
                    return cfResults;
                }
            }
        }

        // 2. Rule-based fallback: same category, excluding the current product, newest first
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (product.getCategory() == null) {
            return Collections.emptyList();
        }

        return productRepository
                .findByCategoryIdExcluding(product.getCategory().getId(), productId, PageRequest.of(0, limit))
                .stream()
                .map(ProductDto::fromEntity)
                .toList();
    }

    /**
     * Returns cart-based recommendations for the given user derived from their order history,
     * using item-based collaborative filtering.
     *
     * <p>Algorithm:
     * <ol>
     *   <li>Load the user's past order items → their purchased product IDs.</li>
     *   <li>Find other users who ordered those same products.</li>
     *   <li>Collect products those users also ordered, excluding already-purchased ones.</li>
     *   <li>Rank by co-occurrence frequency and return the top {@code limit} results.</li>
     *   <li>Fall back to empty list on cold start (no order history).</li>
     * </ol>
     */
    @Override
    public List<ProductDto> getCartRecommendations(Long userId, int limit) {
        List<OrderItem> userItems = orderItemRepository.findByOrderUserId(userId);
        if (userItems.isEmpty()) {
            // Cold start — no purchase history
            return Collections.emptyList();
        }

        Set<Long> purchasedProductIds = userItems.stream()
                .map(oi -> oi.getProduct().getId())
                .collect(Collectors.toSet());

        // Find other users who bought any of the same products
        Set<Long> similarUserIds = purchasedProductIds.stream()
                .flatMap(pid -> orderItemRepository.findByProductId(pid).stream())
                .map(oi -> oi.getOrder().getUser().getId())
                .filter(uid -> !uid.equals(userId))
                .collect(Collectors.toSet());

        if (similarUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Count co-purchase frequency of products not yet bought by the user
        Map<Long, Long> frequency = new HashMap<>();
        for (Long similarUserId : similarUserIds) {
            orderItemRepository.findByOrderUserId(similarUserId).stream()
                    .filter(oi -> !purchasedProductIds.contains(oi.getProduct().getId()))
                    .forEach(oi -> frequency.merge(oi.getProduct().getId(), 1L, Long::sum));
        }

        if (frequency.isEmpty()) {
            return Collections.emptyList();
        }

        return frequency.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .map(e -> productRepository.findById(e.getKey()))
                .filter(opt -> opt.isPresent())
                .map(opt -> ProductDto.fromEntity(opt.get()))
                .toList();
    }
}
