package tz.ac.mzumbe.chuosokoni.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Category;
import tz.ac.mzumbe.chuosokoni.model.Product;
import tz.ac.mzumbe.chuosokoni.model.ProductImage;
import tz.ac.mzumbe.chuosokoni.model.Seller;
import tz.ac.mzumbe.chuosokoni.repository.CategoryRepository;
import tz.ac.mzumbe.chuosokoni.repository.ProductImageRepository;
import tz.ac.mzumbe.chuosokoni.repository.ProductRepository;
import tz.ac.mzumbe.chuosokoni.repository.SellerRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    /**
     * Submits a new product for administrative review and price assessment[cite: 2, 3].
     */
    @Transactional
    public Map<String, Object> createProduct(
            Integer sellerId,
            Integer categoryId,
            String name,
            String description,
            String unit,
            Integer stockQuantity
    ) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller profile not found with ID: " + sellerId));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

        Product product = new Product();
        product.setSeller(seller);
        product.setCategory(category);
        product.setName(name);
        product.setDescription(description);
        product.setUnit(unit != null ? unit : "item");
        product.setStockQuantity(stockQuantity != null ? stockQuantity : 0);
        product.setStatus("PENDING"); // Initial status before market research / price assessment approval[cite: 2, 3]

        Product savedProduct = productRepository.save(product);
        return buildProductResponseMap(savedProduct);
    }
    
    /**
     * Retrieves all approved and active products for marketplace browsing (FR-4.3).
     */
    public List<Map<String, Object>> getActiveProducts() {
        List<Product> products = productRepository.findByStatusIn(List.of("APPROVED", "ACTIVE"));
        return products.stream().map(this::buildProductResponseMap).toList();
    }

    /**
     * Adds an image URL/path to a product and sets primary image status[cite: 2].
     */
    @Transactional
    public Map<String, Object> addProductImage(Integer productId, String imageUrl, Boolean isPrimary) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        if (Boolean.TRUE.equals(isPrimary)) {
            // Reset existing primary images for this product
            List<ProductImage> existingImages = productImageRepository.findByProduct(product);
            for (ProductImage img : existingImages) {
                if (Boolean.TRUE.equals(img.getIsPrimary())) {
                    img.setIsPrimary(false);
                    productImageRepository.save(img);
                }
            }
        }

        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setImageUrl(imageUrl);
        image.setIsPrimary(Boolean.TRUE.equals(isPrimary));

        productImageRepository.save(image);
        return buildProductResponseMap(product);
    }
    
    /**
     * Retrieves detailed information for a specific product by ID.
     */
    public Map<String, Object> getProductById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        return buildProductResponseMap(product);
    }

    /**
     * Updates inventory stock quantity for a product[cite: 2].
     */
    @Transactional
    public Map<String, Object> updateStockQuantity(Integer productId, Integer newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        product.setStockQuantity(newStock);
        product.setUpdatedAt(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);

        return buildProductResponseMap(savedProduct);
    }
    
    /**
     * Submits a new product listing to the administrative review queue (FR-4.3).
     */
    @Transactional
    public Map<String, Object> createProduct(
            Integer sellerUserId,
            Integer categoryId,
            String name,
            String description,
            BigDecimal targetRetailPrice,
            Integer stockQuantity
    ) {
        Seller seller = sellerRepository.findByUserUserId(sellerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Seller profile not found for user ID: " + sellerUserId));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

        Product product = new Product();
        product.setSeller(seller);
        product.setCategory(category);
        product.setName(name);
        product.setDescription(description);
        product.setTargetRetailPrice(targetRetailPrice);
        product.setStockQuantity(stockQuantity != null ? stockQuantity : 0);
        product.setStatus("PENDING"); // Pending admin pricing assessment

        Product savedProduct = productRepository.save(product);
        return buildProductResponseMap(savedProduct);
    }

    /**
     * Admin Operation: Updates listing status (e.g., PENDING, APPROVED, REJECTED, ARCHIVED)[cite: 2, 3].
     */
    @Transactional
    public Map<String, Object> updateProductStatus(Integer productId, String newStatus) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        String sanitizedStatus = newStatus.toUpperCase().trim();
        if (!List.of("PENDING", "APPROVED", "REJECTED", "ARCHIVED").contains(sanitizedStatus)) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus);
        }

        product.setStatus(sanitizedStatus);
        product.setUpdatedAt(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);

        return buildProductResponseMap(savedProduct);
    }

    /**
     * Retrieves products associated with a seller[cite: 2].
     */
    public List<Map<String, Object>> getProductsBySeller(Integer sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found with ID: " + sellerId));

        List<Product> products = productRepository.findBySeller(seller);
        return products.stream().map(this::buildProductResponseMap).toList();
    }

    /**
     * Retrieves all approved active products by category[cite: 2, 3].
     */
    public List<Map<String, Object>> getApprovedProductsByCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

        List<Product> products = productRepository.findByCategoryAndStatus(category, "APPROVED");
        return products.stream().map(this::buildProductResponseMap).toList();
    }

    /**
     * Helper to package product details and image lists into map responses.
     */
    private Map<String, Object> buildProductResponseMap(Product product) {
        Map<String, Object> map = new HashMap<>();
        map.put("productId", product.getProductId());
        map.put("sellerId", product.getSeller().getSellerId());
        map.put("sellerBusinessName", product.getSeller().getBusinessName());
        map.put("categoryId", product.getCategory().getCategoryId());
        map.put("categoryName", product.getCategory().getName());
        map.put("name", product.getName());
        map.put("description", product.getDescription());
        map.put("unit", product.getUnit());
        map.put("stockQuantity", product.getStockQuantity());
        map.put("status", product.getStatus());
        map.put("createdAt", product.getCreatedAt());
        map.put("updatedAt", product.getUpdatedAt());

        List<ProductImage> images = productImageRepository.findByProduct(product);
        List<Map<String, Object>> imageList = images.stream().map(img -> {
            Map<String, Object> imgMap = new HashMap<>();
            imgMap.put("imageId", img.getImageId());
            imgMap.put("imageUrl", img.getImageUrl());
            imgMap.put("isPrimary", img.getIsPrimary());
            return imgMap;
        }).toList();

        map.put("images", imageList);
        return map;
    }
}