package tz.ac.mzumbe.chuosokoni.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Agent;
import tz.ac.mzumbe.chuosokoni.model.ReferralCode;
import tz.ac.mzumbe.chuosokoni.model.User;
import tz.ac.mzumbe.chuosokoni.repository.AgentRepository;
import tz.ac.mzumbe.chuosokoni.repository.ReferralCodeRepository;
import tz.ac.mzumbe.chuosokoni.repository.UserRepository;

@Service
public class AgentService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReferralCodeRepository referralCodeRepository;

    /**
     * Retrieves agent profile details by user ID.
     */
    public Map<String, Object> getAgentProfileByUserId(Integer userId) {
        Agent agent = agentRepository.findByUserUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Agent profile not found for user ID: " + userId));

        return buildAgentResponseMap(agent);
    }

    /**
     * Generates a new unique referral code for an existing agent.
     */
    @Transactional
    public Map<String, Object> regenerateReferralCode(Integer agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found with ID: " + agentId));

        String newCode = "AGT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        while (referralCodeRepository.existsByCode(newCode)) {
            newCode = "AGT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        // Deactivate current default codes if any exist
        ReferralCode existingCode = referralCodeRepository.findByAgentAndIsDefaultTrue(agent).orElse(null);
        if (existingCode != null) {
            existingCode.setIsDefault(false);
            referralCodeRepository.save(existingCode);
        }

        // Create and save new referral code record
        ReferralCode referralCode = new ReferralCode();
        referralCode.setAgent(agent);
        referralCode.setCode(newCode);
        referralCode.setIsDefault(true);
        referralCode.setCreatedAt(LocalDateTime.now());
        referralCodeRepository.save(referralCode);

        return buildAgentResponseMap(agent);
    }

    /**
     * Increments referral count and checks for suspicious activity.
     */
    @Transactional
    public void incrementReferralCount(Agent agent) {
        agent.setTotalReferrals(agent.getTotalReferrals() + 1);

        if (agent.getTotalReferrals() > 500 && "JUNIOR".equalsIgnoreCase(agent.getAgentLevel())) {
            agent.setStatus("FLAGGED");
        }

        agentRepository.save(agent);
    }

    /**
     * Admin Operation: Updates agent level and status.
     */
    @Transactional
    public Map<String, Object> updateAgentStatus(Integer agentId, String level, String status) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found with ID: " + agentId));

        if (level != null && !level.trim().isEmpty()) {
            agent.setAgentLevel(level.toUpperCase().trim());
        }

        if (status != null && !status.trim().isEmpty()) {
            agent.setStatus(status.toUpperCase().trim());
        }

        Agent savedAgent = agentRepository.save(agent);
        return buildAgentResponseMap(savedAgent);
    }

    /**
     * Fetches dashboard details for an agent.
     */
    public Map<String, Object> getAgentDashboard(Integer userId) {
        Agent agent = agentRepository.findByUserUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Agent profile not found for user ID: " + userId));

        return buildAgentResponseMap(agent);
    }

    /**
     * Fetches all registered referred users attributed to this specific agent.
     */
    public List<Map<String, Object>> getReferredUsers(Integer userId) {
        User agentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        List<User> referredUsers = userRepository.findByReferredBy(agentUser);
        return referredUsers.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", user.getUserId());
            map.put("fullName", user.getFullName());
            map.put("email", user.getEmail());
            map.put("status", user.getStatus());
            map.put("createdAt", user.getCreatedAt());
            return map;
        }).toList();
    }

    /**
     * Provisions a new agent profile for a user account along with an initial ReferralCode.
     */
    @Transactional
    public Map<String, Object> createAgentProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (agentRepository.findByUserUserId(userId).isPresent()) {
            throw new IllegalStateException("Agent profile already exists for user ID: " + userId);
        }

        Agent agent = new Agent();
        agent.setUser(user);
        agent.setAgentLevel("BRONZE");
        agent.setTotalReferrals(0);
        agent.setTotalCommissions(BigDecimal.ZERO);
        agent.setStatus("ACTIVE");
        agent.setCreatedAt(LocalDateTime.now());

        Agent savedAgent = agentRepository.save(agent);

        // Generate and store ReferralCode entity
        String codeValue = "AGT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        while (referralCodeRepository.existsByCode(codeValue)) {
            codeValue = "AGT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        ReferralCode referralCode = new ReferralCode();
        referralCode.setAgent(savedAgent);
        referralCode.setCode(codeValue);
        referralCode.setIsDefault(true);
        referralCode.setCreatedAt(LocalDateTime.now());
        referralCodeRepository.save(referralCode);

        return buildAgentResponseMap(savedAgent);
    }

    /**
     * Helper to construct response structures for agent outputs.
     */
    private Map<String, Object> buildAgentResponseMap(Agent agent) {
        // Query active referral code from ReferralCodeRepository
        String codeStr = referralCodeRepository.findByAgentAndIsDefaultTrue(agent)
                .or(() -> referralCodeRepository.findFirstByAgentOrderByCreatedAtDesc(agent))
                .map(ReferralCode::getCode)
                .orElse(null);

        Map<String, Object> map = new HashMap<>();
        map.put("agentId", agent.getAgentId());
        map.put("userId", agent.getUser().getUserId());
        map.put("fullName", agent.getUser().getFullName());
        map.put("referralCode", codeStr);
        map.put("agentLevel", agent.getAgentLevel());
        map.put("totalReferrals", agent.getTotalReferrals());
        map.put("totalCommission", agent.getTotalCommissions() != null ? agent.getTotalCommissions() : BigDecimal.ZERO);
        map.put("status", agent.getStatus());
        map.put("createdAt", agent.getCreatedAt());
        return map;
    }
}