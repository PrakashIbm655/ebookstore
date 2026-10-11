package com.ebookstore.service;

import com.ebookstore.dto.AddToCartRequest;
import com.ebookstore.dto.CartDto;
import com.ebookstore.dto.CartItemDto;
import com.ebookstore.dto.UpdateCartItemRequest;
import com.ebookstore.exception.ResourceNotFoundException;
import com.ebookstore.model.Cart;
import com.ebookstore.model.CartItem;
import com.ebookstore.model.Product;
import com.ebookstore.model.User;
import com.ebookstore.repository.CartItemRepository;
import com.ebookstore.repository.CartRepository;
import com.ebookstore.repository.ProductRepository;
import com.ebookstore.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /** Returns the current user's cart, creating it if it does not yet exist. */
    public CartDto getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return toDto(cart);
    }

    /**
     * Adds a product to the cart. If the product is already in the cart its
     * quantity is incremented by the requested amount; otherwise a new CartItem
     * is created. Stock is validated before any change is made.
     */
    public CartDto addItem(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + request.getProductId()));

        // determine total desired quantity after this operation
        int existingQty = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .map(CartItem::getQuantity)
                .orElse(0);

        int desiredQty = existingQty + request.getQuantity();

        if (product.getStockQty() < desiredQty) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Insufficient stock for product '" + product.getTitle()
                    + "'. Available: " + product.getStockQty()
                    + ", requested total: " + desiredQty);
        }

        cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .ifPresentOrElse(
                        item -> item.setQuantity(desiredQty),
                        () -> cartItemRepository.save(new CartItem(cart, product, request.getQuantity()))
                );

        cart.touch();
        cartRepository.save(cart);

        return toDto(cart);
    }

    /**
     * Updates the quantity of an existing cart item. The item must belong to
     * the requesting user's cart.
     */
    public CartDto updateItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        CartItem item = getItemOwnedByUser(userId, itemId);

        Product product = item.getProduct();
        if (product.getStockQty() < request.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Insufficient stock for product '" + product.getTitle()
                    + "'. Available: " + product.getStockQty()
                    + ", requested: " + request.getQuantity());
        }

        item.setQuantity(request.getQuantity());
        Cart cart = item.getCart();
        cart.touch();
        cartRepository.save(cart);

        return toDto(cart);
    }

    /**
     * Removes a single item from the cart. The item must belong to the
     * requesting user's cart.
     */
    public CartDto removeItem(Long userId, Long itemId) {
        CartItem item = getItemOwnedByUser(userId, itemId);
        Cart cart = item.getCart();

        cartItemRepository.delete(item);
        cart.touch();
        cartRepository.save(cart);

        return toDto(cart);
    }

    /**
     * Deletes all items in the user's cart. Called by the Order module after a
     * successful order placement; creates the cart first if it somehow does not
     * exist so the call is always idempotent.
     */
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
            cartItemRepository.deleteAll(items);
            cart.touch();
            cartRepository.save(cart);
        });
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
            return cartRepository.save(new Cart(user));
        });
    }

    private CartItem getItemOwnedByUser(Long userId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Cart item does not belong to current user");
        }
        return item;
    }

    /** Builds a {@link CartDto} including all line items and the running total. */
    CartDto toDto(Cart cart) {
        List<CartItemDto> itemDtos = cartItemRepository.findByCartId(cart.getId()).stream()
                .map(item -> new CartItemDto(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getTitle(),
                        item.getProduct().getImageUrl(),
                        item.getProduct().getPrice(),
                        item.getQuantity()))
                .toList();

        return new CartDto(
                cart.getId(),
                cart.getUser().getId(),
                itemDtos,
                cart.getUpdatedAt());
    }
}
