package tz.ac.mzumbe.chuosokoni.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Retrieves all approved active products for marketplace feed browsing (FR-4.3).
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getActiveProducts() {
        try {
            List<Map<String, Object>> products = productService.getActiveProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves detailed information for a specific product by ID, including associated image URLs.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Integer productId) {
        try {
            Map<String, Object> product = productService.getProductById(productId);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve product details: " + e.getMessage()));
        }
    }

    /**
     * Seller Operation: Submits a new product listing to the administrative price assessment queue (FR-4.3).
     * Expected JSON body: { "sellerUserId", "categoryId", "name", "description", "targetRetailPrice", "stockQuantity" }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Map<String, Object> productRequest) {
        try {
            if (productRequest.get("sellerUserId") == null || productRequest.get("categoryId") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "sellerUserId and categoryId are required."));
            }

            Integer sellerUserId = Integer.parseInt(productRequest.get("sellerUserId").toString());
            Integer categoryId = Integer.parseInt(productRequest.get("categoryId").toString());
            String name = (String) productRequest.get("name");
            String description = (String) productRequest.get("description");
            BigDecimal targetRetailPrice = new BigDecimal(productRequest.get("targetRetailPrice").toString());
            Integer stockQuantity = Integer.parseInt(productRequest.get("stockQuantity").toString());

            Map<String, Object> response = productService.createProduct(
                    sellerUserId, categoryId, name, description, targetRetailPrice, stockQuantity
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit product: " + e.getMessage()));
        }
    }

    /**
     * Attaches an image URL to a product listing.
     * Expected JSON body: { "imageUrl", "isPrimary" }
     */
    @PostMapping("/{productId}/images")
    public ResponseEntity<Map<String, Object>> addProductImage(
            @PathVariable Integer productId,
            @RequestBody Map<String, Object> imageRequest
    ) {
        try {
            String imageUrl = (String) imageRequest.get("imageUrl");
            Boolean isPrimary = imageRequest.get("isPrimary") != null && Boolean.parseBoolean(imageRequest.get("isPrimary").toString());

            Map<String, Object> response = productService.addProductImage(productId, imageUrl, isPrimary);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add product image: " + e.getMessage()));
        }
    }

    /**
     * Seller Operation: Updates the remaining inventory stock level for a product.
     * Expected JSON body: { "stockQuantity" }
     */
    @PutMapping("/{productId}/stock")
    public ResponseEntity<Map<String, Object>> updateStockQuantity(
            @PathVariable Integer productId,
            @RequestBody Map<String, Object> stockRequest
    ) {
        try {
            if (stockRequest.get("stockQuantity") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "stockQuantity is required."));
            }

            Integer newQuantity = Integer.parseInt(stockRequest.get("stockQuantity").toString());
            Map<String, Object> response = productService.updateStockQuantity(productId, newQuantity);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update stock quantity: " + e.getMessage()));
        }
    }
}