package tz.ac.mzumbe.chuosokoni.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByUser_UserId(Integer userId);

    boolean existsByUser_UserId(Integer userId);

    long countByCustomerType(String customerType);
}
