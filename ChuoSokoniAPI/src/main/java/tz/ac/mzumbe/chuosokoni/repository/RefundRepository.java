package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Refund;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Integer> {

    List<Refund> findByPayment_PaymentId(Integer paymentId);

    List<Refund> findByOrder_OrderId(Integer orderId);

    List<Refund> findByStatus(String status);

    List<Refund> findByReason(String reason);

    List<Refund> findByOrder_OrderIdAndStatus(
            Integer orderId,
            String status
    );
}
