package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Listing;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Integer> {

    List<Listing> findByProduct_ProductId(Integer productId);

    List<Listing> findBySeller_SellerId(Integer sellerId);

    List<Listing> findByCategory_CategoryId(Integer categoryId);

    List<Listing> findByStatus(String status);

    List<Listing> findByListingType(String listingType);

    List<Listing> findByTransactionModel(String transactionModel);

    List<Listing> findBySeller_SellerIdAndStatus(
            Integer sellerId,
            String status
    );
}
