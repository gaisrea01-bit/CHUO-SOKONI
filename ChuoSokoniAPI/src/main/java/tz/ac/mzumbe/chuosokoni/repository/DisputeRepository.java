package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Dispute;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Integer> {

    List<Dispute> findByOrder_OrderId(Integer orderId);

    List<Dispute> findByStatusIn(List<String> statuses);

    List<Dispute> findByInitiatedBy_UserId(Integer userId);

    List<Dispute> findByResolution(String resolution);

    List<Dispute> findByOrder_OrderIdAndResolution(
            Integer orderId,
            String resolution
    );
}
