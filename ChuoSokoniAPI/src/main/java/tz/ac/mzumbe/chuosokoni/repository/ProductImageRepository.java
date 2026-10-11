package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Product;
import tz.ac.mzumbe.chuosokoni.model.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    List<ProductImage> findByProduct_ProductId(Integer productId);
    
    List<ProductImage> findByProduct(Product product);

    Optional<ProductImage> findByProduct_ProductIdAndIsPrimaryTrue(
            Integer productId
    );

    List<ProductImage> findByProduct_ProductIdOrderByImageIdAsc(
            Integer productId
    );
}
