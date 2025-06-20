package com.lighting.repository;

import com.lighting.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    // Find products that are not deleted
    @Query("{'deletedAt': null}")
    List<Product> findAllActive();

    // Find by category
    List<Product> findByCategoryAndDeletedAtIsNull(String category);

    // Find by type
    List<Product> findByTypeAndDeletedAtIsNull(String type);

    // Find by name containing (case insensitive search)
    @Query("{'name': {$regex: ?0, $options: 'i'}, 'deletedAt': null}")
    List<Product> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name);

    // Find by ID and not deleted
    Optional<Product> findByIdAndDeletedAtIsNull(String id);

    // Find products with stock greater than 0
    @Query("{'stock': {$gt: 0}, 'deletedAt': null}")
    List<Product> findAllInStock();

    // Find by category and type
    List<Product> findByCategoryAndTypeAndDeletedAtIsNull(String category, String type);

    // Find by price range
    @Query("{'price': {$gte: ?0, $lte: ?1}, 'deletedAt': null}")
    List<Product> findByPriceRangeAndDeletedAtIsNull(Double minPrice, Double maxPrice);

    // Count products by category
    @Query(value = "{'category': ?0, 'deletedAt': null}", count = true)
    long countByCategoryAndDeletedAtIsNull(String category);

    // Soft delete by setting deletedAt
    @Query("{'_id': ?0}")
    void softDeleteById(String id);
}
