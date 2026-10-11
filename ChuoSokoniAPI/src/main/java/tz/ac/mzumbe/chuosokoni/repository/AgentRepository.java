package tz.ac.mzumbe.chuosokoni.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Agent;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Integer> {

    Optional<Agent> findByUserUserId(Integer userId);

    boolean existsByUserUserId(Integer userId);

    long countByStatus(String status);

    long countByAgentLevel(String agentLevel);
}
