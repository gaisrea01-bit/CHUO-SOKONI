package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Withdrawal;

@Repository
public interface WithdrawalRepository
        extends JpaRepository<Withdrawal, Integer> {

    List<Withdrawal> findByUser_UserId(Integer userId);

    List<Withdrawal> findByStatus(String status);

    List<Withdrawal> findByUser_UserIdAndStatus(
            Integer userId,
            String status
    );

    Optional<Withdrawal> findByReferenceNumber(String referenceNumber);
}
