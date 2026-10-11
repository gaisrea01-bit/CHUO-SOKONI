package tz.ac.mzumbe.chuosokoni.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.InventoryStock;

@Repository
public interface InventoryStockRepository
        extends JpaRepository<InventoryStock, Integer> {

    Optional<InventoryStock> findByProduct_ProductId(Integer productId);

    boolean existsByProduct_ProductId(Integer productId);
}
