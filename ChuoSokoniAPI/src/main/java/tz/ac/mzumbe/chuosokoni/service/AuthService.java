package tz.ac.mzumbe.chuosokoni.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Role;
import tz.ac.mzumbe.chuosokoni.model.University;
import tz.ac.mzumbe.chuosokoni.model.User;
import tz.ac.mzumbe.chuosokoni.repository.ReferralCodeRepository;
import tz.ac.mzumbe.chuosokoni.repository.RoleRepository;
import tz.ac.mzumbe.chuosokoni.repository.UniversityRepository;
import tz.ac.mzumbe.chuosokoni.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private ReferralCodeRepository referralCodeRepository;

    @Autowired
    private AgentService agentService;

    /**
     * Registers a new user and assigns them to the requested role (e.g.,
     * CUSTOMER, AGENT, SELLER, INVESTOR).
     */
    @Transactional
    public Map<String, Object> registerUser(
            String fullName,
            String email,
            String password,
            String phoneNumber,
            Integer universityId,
            String roleName,
            String referralCode
    ) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Email and password are required.");
        }

        if (userRepository.findByEmail(email.trim().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        // Map roleName to Role entity (defaults to CUSTOMER if null or empty)
        String targetRoleName = (roleName != null && !roleName.trim().isEmpty())
                                ? roleName.toUpperCase().trim()
                                : "CUSTOMER";

        Role role = roleRepository.findByRoleName(targetRoleName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role specified: " + targetRoleName));

        University university = null;
        if (universityId != null) {
            university = universityRepository.findById(universityId).orElse(null);
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(password); // In production, wrap with BCryptPasswordEncoder
        user.setPhone(phoneNumber);
        user.setUniversity(university);
        user.setRole(role);
        user.setStatus("ACTIVE"); // Default active state

        // Handle referral code attribution via ReferralCode entity repository
        if (referralCode != null && !referralCode.trim().isEmpty()) {
            String sanitizedCode = referralCode.trim().toUpperCase();
            user.setReferralCodeUsed(sanitizedCode);

            referralCodeRepository.findByCode(sanitizedCode).ifPresent(refCode -> {
                if (refCode.getAgent() != null && refCode.getAgent().getUser() != null) {
                    user.setReferredBy(refCode.getAgent().getUser());
                    // Increment agent total referrals counter
                    agentService.incrementReferralCount(refCode.getAgent());
                }
            });
        }

        User savedUser = userRepository.save(user);

        // Auto-provision agent profile and referral code if registering as AGENT
        if ("AGENT".equalsIgnoreCase(targetRoleName)) {
            agentService.createAgentProfile(savedUser.getUserId());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", savedUser.getUserId());
        response.put("fullName", savedUser.getFullName());
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole().getRoleName());
        response.put("status", savedUser.getStatus());
        response.put("message", "User registered successfully.");

        return response;
    }

    /**
     * Authenticates credentials and returns user details with JWT token.
     */
    public Map<String, Object> authenticateUser(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password are required.");
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new SecurityException("Invalid email or password."));

        if (!password.equals(user.getPasswordHash())) {
            throw new SecurityException("Invalid email or password.");
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new SecurityException("Account is inactive or suspended.");
        }

        String accessToken = "jwt_access_token_" + UUID.randomUUID().toString();
        String refreshToken = "jwt_refresh_token_" + UUID.randomUUID().toString();

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().getRoleName());
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

        return response;
    }

    /**
     * Refreshes an expired JWT access token.
     */
    public Map<String, Object> refreshToken(String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("jwt_refresh_token_")) {
            throw new SecurityException("Invalid or expired refresh token.");
        }

        String newAccessToken = "jwt_access_token_" + UUID.randomUUID().toString();

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("refreshToken", refreshToken);
        response.put("refreshedAt", LocalDateTime.now());

        return response;
    }
}
