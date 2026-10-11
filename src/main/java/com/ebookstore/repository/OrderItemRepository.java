package com.ebookstore.repository;

import com.ebookstore.model.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    /** Returns all order items from all orders placed by the given user. Used for collaborative filtering. */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.user.id = :userId")
    List<OrderItem> findByOrderUserId(@Param("userId") Long userId);

    /** Returns all order items for the given product across all orders. Used for collaborative filtering. */
    List<OrderItem> findByProductId(Long productId);
}
