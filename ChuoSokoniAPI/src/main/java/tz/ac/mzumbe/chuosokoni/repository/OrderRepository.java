package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Order;
import tz.ac.mzumbe.chuosokoni.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Optional<Order> findByTransaction_TransactionId(Integer transactionId);

    List<Order> findByBuyer_UserId(Integer buyerId);

    List<Order> findBySeller_UserId(Integer sellerId);

    List<Order> findByBuyer(User buyer);

    List<Order> findByOrderStatus(String orderStatus);

    List<Order> findByPaymentStatus(String paymentStatus);

    List<Order> findByBuyer_UserIdAndOrderStatus(
            Integer buyerId,
            String orderStatus
    );

    List<Order> findBySeller_UserIdAndOrderStatus(
            Integer sellerId,
            String orderStatus
    );
}
