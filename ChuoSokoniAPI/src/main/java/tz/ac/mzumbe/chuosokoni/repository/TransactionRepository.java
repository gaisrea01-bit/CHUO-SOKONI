package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Transaction;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByListing_ListingId(Integer listingId);

    List<Transaction> findByBuyer_UserId(Integer buyerId);

    List<Transaction> findBySeller_UserId(Integer sellerId);

    List<Transaction> findByAgent_AgentId(Integer agentId);

    List<Transaction> findByStatus(String status);

    List<Transaction> findByTransactionModel(String transactionModel);

    List<Transaction> findByBuyer_UserIdAndStatus(
            Integer buyerId,
            String status
    );

    List<Transaction> findBySeller_UserIdAndStatus(
            Integer sellerId,
            String status
    );
}
