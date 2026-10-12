package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.InvestorReturn;

@Repository
public interface PaymentRefundRepository
        extends JpaRepository<InvestorReturn, Integer> {

    List<InvestorReturn> findByTransaction_TransactionId(Integer transactionId);

    List<InvestorReturn> findByOrder_OrderId(Integer orderId);

    List<InvestorReturn> findByStatus(String status);

    List<InvestorReturn> findByOrder_OrderIdAndStatus(
            Integer orderId,
            String status
    );
}
