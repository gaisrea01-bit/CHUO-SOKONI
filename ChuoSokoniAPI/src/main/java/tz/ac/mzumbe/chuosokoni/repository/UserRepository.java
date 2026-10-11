package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Role;
import tz.ac.mzumbe.chuosokoni.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);
    
    List<User> findByRole(Role role);

    Optional<User> findByReferralCodeUsed(String referralCodeUsed);
    
    List<User> findByReferredBy(User referredBy);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
