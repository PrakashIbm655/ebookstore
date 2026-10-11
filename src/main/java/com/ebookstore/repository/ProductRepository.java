package com.ebookstore.repository;

import com.ebookstore.model.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * Returns products in the same category as {@code categoryId}, excluding the product
     * identified by {@code excludeId}, ordered by creation date descending.
     * Used by the rule-based recommendation fallback.
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id <> :excludeId ORDER BY p.createdAt DESC")
    List<Product> findByCategoryIdExcluding(
            @Param("categoryId") Long categoryId,
            @Param("excludeId") Long excludeId,
            Pageable pageable);
}
