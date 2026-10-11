package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.StockMovement;

@Repository
public interface StockMovementRepository
        extends JpaRepository<StockMovement, Integer> {

    List<StockMovement> findByProduct_ProductId(Integer productId);

    List<StockMovement> findByReferenceId(Integer referenceId);

    List<StockMovement> findByType(String type);
}
