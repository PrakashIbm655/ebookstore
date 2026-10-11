package com.ebookstore.controller;

import com.ebookstore.dto.ProductDto;
import com.ebookstore.security.AuthenticatedUser;
import com.ebookstore.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints for the recommendation engine.
 *
 * <p>All endpoints in this controller require JWT authentication.
 */
@RestController
@RequestMapping("/recommendations")
@Tag(name = "Recommendations", description = "AI-assisted product recommendations endpoints")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Returns up to {@code limit} product recommendations based on the authenticated user's
     * order history (item-based collaborative filtering).
     *
     * @param limit       maximum number of recommendations (default 5)
     * @param currentUser the JWT principal injected by Spring Security
     * @return list of recommended {@link ProductDto}
     */
    @GetMapping("/cart")
    @Operation(summary = "Get cart recommendations based on user order history")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<ProductDto>> getCartRecommendations(
            @RequestParam(defaultValue = "5") int limit,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        List<ProductDto> recommendations =
                recommendationService.getCartRecommendations(currentUser.userId(), limit);
        return ResponseEntity.ok(recommendations);
    }
}
