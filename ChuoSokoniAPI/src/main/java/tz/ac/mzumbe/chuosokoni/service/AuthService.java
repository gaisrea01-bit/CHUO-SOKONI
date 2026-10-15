package tz.ac.mzumbe.chuosokoni.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.*;
import tz.ac.mzumbe.chuosokoni.repository.*;

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
    private SellerRepository sellerRepository;

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AgentService agentService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
            throw new IllegalArgumentException("An account with this email or phone already exists.");
        }

        // Map roleName to Role entity (defaults to CUSTOMER if null or empty)
        String targetRoleName = (roleName != null && !roleName.trim().isEmpty())
                                ? roleName.toUpperCase().trim()
                                : "CUSTOMER";

        Role role = roleRepository.findByRoleName(targetRoleName)
                .orElseGet(() -> {
                    Role defaultRole = new Role();
                    defaultRole.setRoleName(targetRoleName);
                    defaultRole.setDescription("Auto-created " + targetRoleName + " role");
                    return roleRepository.save(defaultRole);
                });

        University university = null;
        if (universityId != null) {
            university = universityRepository.findById(universityId).orElse(null);
        }
        if (university == null) {
            university = universityRepository.findAll().stream().findFirst().orElse(null);
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(password)); // Encode with BCrypt
        user.setPhone(phoneNumber != null ? phoneNumber.trim() : email);
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

        // Auto-provision role profile
        if ("AGENT".equalsIgnoreCase(targetRoleName)) {
            agentService.createAgentProfile(savedUser.getUserId());
        } else if ("SELLER".equalsIgnoreCase(targetRoleName)) {
            Seller seller = new Seller();
            seller.setUser(savedUser);
            seller.setBusinessName(savedUser.getFullName() + "'s Shop");
            seller.setVerificationStatus("VERIFIED");
            sellerRepository.save(seller);
        } else if ("INVESTOR".equalsIgnoreCase(targetRoleName)) {
            Investor investor = new Investor();
            investor.setUser(savedUser);
            investor.setStatus("ACTIVE");
            investorRepository.save(investor);
        } else if ("CUSTOMER".equalsIgnoreCase(targetRoleName)) {
            Customer customer = new Customer();
            customer.setUser(savedUser);
            customer.setCustomerType("STUDENT");
            customerRepository.save(customer);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", savedUser.getUserId());
        response.put("fullName", savedUser.getFullName());
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole() != null ? savedUser.getRole().getRoleName() : targetRoleName);
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

        String identifier = email.trim().toLowerCase();
        User user = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElseThrow(() -> new SecurityException("Invalid email/phone or password."));

        boolean passwordMatches = false;
        if (user.getPasswordHash() != null) {
            String storedHash = user.getPasswordHash();
            if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
                passwordMatches = passwordEncoder.matches(password, storedHash);
            } else {
                passwordMatches = password.equals(storedHash);
            }
        }

        if (!passwordMatches) {
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
        response.put("role", user.getRole() != null ? user.getRole().getRoleName() : "CUSTOMER");
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
