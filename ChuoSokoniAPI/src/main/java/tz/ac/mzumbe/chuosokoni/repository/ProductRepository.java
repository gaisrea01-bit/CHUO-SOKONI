package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Category;
import tz.ac.mzumbe.chuosokoni.model.Product;
import tz.ac.mzumbe.chuosokoni.model.Seller;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByStatusIn(List<String> statuses);

    List<Product> findBySeller_SellerId(Integer sellerId);

    List<Product> findBySeller(Seller seller);

    List<Product> findByCategoryAndStatus(Category category, String status);

    List<Product> findByCategory_CategoryId(Integer categoryId);

    List<Product> findBySeller_SellerIdAndCategory_CategoryId(
            Integer sellerId,
            Integer categoryId
    );

    List<Product> findByStatus(String status);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findBySeller_SellerIdAndStatus(
            Integer sellerId,
            String status
    );
}
