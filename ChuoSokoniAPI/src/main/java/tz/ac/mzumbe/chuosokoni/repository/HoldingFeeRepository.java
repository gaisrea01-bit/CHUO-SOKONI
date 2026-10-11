package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.HoldingFee;

@Repository
public interface HoldingFeeRepository
        extends JpaRepository<HoldingFee, Integer> {

    List<HoldingFee> findByListing_ListingId(Integer listingId);

    List<HoldingFee> findByStatus(String status);

    List<HoldingFee> findByListing_ListingIdAndStatus(
            Integer listingId,
            String status
    );
}
