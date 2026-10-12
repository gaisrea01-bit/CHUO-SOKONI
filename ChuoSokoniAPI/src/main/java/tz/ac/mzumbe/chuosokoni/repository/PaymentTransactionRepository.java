package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.PaymentTransaction;

@Repository
public interface PaymentTransactionRepository
        extends JpaRepository<PaymentTransaction, Integer> {

    List<PaymentTransaction> findByPayment_PaymentId(Integer paymentId);

    List<PaymentTransaction> findByTransactionId(String transactionId);

    List<PaymentTransaction> findByStatus(String status);

    List<PaymentTransaction> findByPayment_PaymentIdAndStatus(
            Integer paymentId,
            String status
    );
}
