package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Order;
import tz.ac.mzumbe.chuosokoni.model.OrderItem;

@Repository
public interface OrderItemRepository
        extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findByOrder_OrderId(Integer orderId);

    List<OrderItem> findByProduct_ProductId(Integer productId);

    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByOrder_OrderIdAndProduct_ProductId(
            Integer orderId,
            Integer productId
    );
}
