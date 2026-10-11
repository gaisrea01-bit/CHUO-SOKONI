package tz.ac.mzumbe.chuosokoni.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Investor;

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Integer> {

    Optional<Investor> findByUserUserId(Integer userId);

    boolean existsByUserUserId(Integer userId);

    long countByStatus(String status);

    long countByInvestmentType(String investmentType);
}
