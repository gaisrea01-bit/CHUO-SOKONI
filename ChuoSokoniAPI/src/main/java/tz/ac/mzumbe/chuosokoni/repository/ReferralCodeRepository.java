package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Agent;
import tz.ac.mzumbe.chuosokoni.model.ReferralCode;

@Repository
public interface ReferralCodeRepository extends JpaRepository<ReferralCode, Integer> {

    Optional<ReferralCode> findByCode(String code);

    List<ReferralCode> findByAgent_AgentId(Integer agentId);

    Optional<ReferralCode> findByAgent_AgentIdAndIsDefaultTrue(Integer agentId);

    boolean existsByCode(String code);

    List<ReferralCode> findByIsDefault(Boolean isDefault);

    Optional<ReferralCode> findByAgentAndIsDefaultTrue(Agent agent);

    Optional<ReferralCode> findFirstByAgentOrderByCreatedAtDesc(Agent agent);
}
