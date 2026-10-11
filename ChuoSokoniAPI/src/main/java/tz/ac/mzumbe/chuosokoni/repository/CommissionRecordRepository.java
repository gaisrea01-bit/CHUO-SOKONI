package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Agent;
import tz.ac.mzumbe.chuosokoni.model.CommissionRecord;

@Repository
public interface CommissionRecordRepository
        extends JpaRepository<CommissionRecord, Integer> {

    List<CommissionRecord> findByTransactionTransactionId(
            Integer transactionId
    );

    List<CommissionRecord> findByAgent(Agent agent);

    List<CommissionRecord> findByAgentAgentId(Integer agentId);

    List<CommissionRecord> findByInvestorInvestorId(Integer investorId);

    List<CommissionRecord> findByStatus(String status);

    List<CommissionRecord> findByAgentAgentIdAndStatus(
            Integer agentId,
            String status
    );

    List<CommissionRecord> findByInvestorInvestorIdAndStatus(
            Integer investorId,
            String status
    );
}
