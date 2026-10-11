package tz.ac.mzumbe.chuosokoni.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Seller;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {

    Optional<Seller> findByUserUserId(Integer userId);

    Optional<Seller> findByBusinessName(String businessName);

    boolean existsByUserUserId(Integer userId);

    boolean existsByBusinessName(String businessName);

    long countByVerificationStatus(String verificationStatus);

}