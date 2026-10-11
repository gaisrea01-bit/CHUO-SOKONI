package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Order;
import tz.ac.mzumbe.chuosokoni.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByOrder_OrderId(Integer orderId);

    List<Payment> findByPaymentProvider(String paymentProvider);

    Optional<Payment> findByReferenceNumber(String referenceNumber);

    List<Payment> findByOrder(Order order);

    List<Payment> findByPaymentMethod(String paymentMethod);

    List<Payment> findByStatus(String status);

    boolean existsByReferenceNumber(String referenceNumber);
}
