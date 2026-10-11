package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findByUser_UserId(Integer userId);

    Optional<Address> findByUser_UserIdAndIsDefaultTrue(Integer userId);

    List<Address> findByUser_UserIdAndAddressType(
            Integer userId,
            String addressType
    );
}
