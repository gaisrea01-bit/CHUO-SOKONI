package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.InvestorReturn;

@Repository
public interface PaymentRefundRepository
        extends JpaRepository<InvestorReturn, Integer> {

    List<InvestorReturn> findByTransactionTransactionId(Integer transactionId);

    List<InvestorReturn> findByOrderOrderId(Integer orderId);

    List<InvestorReturn> findByStatus(String status);

    List<InvestorReturn> findByOrderOrderIdAndStatus(
            Integer orderId,
            String status
    );
}