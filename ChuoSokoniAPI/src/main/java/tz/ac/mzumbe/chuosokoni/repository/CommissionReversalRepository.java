package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.CommissionReversal;

@Repository
public interface CommissionReversalRepository
        extends JpaRepository<CommissionReversal, Integer> {

    List<CommissionReversal> findByCommissionCommissionId(
            Integer commissionId
    );

    List<CommissionReversal> findByStatus(String status);

    List<CommissionReversal> findByReason(String reason);
}