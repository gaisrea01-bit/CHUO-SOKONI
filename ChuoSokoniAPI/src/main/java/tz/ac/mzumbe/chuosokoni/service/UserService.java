package tz.ac.mzumbe.chuosokoni.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Role;
import tz.ac.mzumbe.chuosokoni.model.University;
import tz.ac.mzumbe.chuosokoni.model.User;
import tz.ac.mzumbe.chuosokoni.repository.RoleRepository;
import tz.ac.mzumbe.chuosokoni.repository.UniversityRepository;
import tz.ac.mzumbe.chuosokoni.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Retrieves user profile details by user_id[cite: 2].
     */
    public Map<String, Object> getUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return buildUserResponseMap(user);
    }

    /**
     * Updates editable user profile fields (Full Name, Phone Number, University)[cite: 2].
     */
    @Transactional
    public Map<String, Object> updateUserProfile(
            Integer userId,
            String fullName,
            String phone,
            Integer universityId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
        }

        if (phone != null && !phone.trim().isEmpty() && !phone.equals(user.getPhone())) {
            if (userRepository.existsByPhone(phone.trim())) {
                throw new IllegalArgumentException("Phone number is already associated with another account.");
            }
            user.setPhone(phone.trim());
        }

        if (universityId != null) {
            University university = universityRepository.findById(universityId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid university ID: " + universityId));
            user.setUniversity(university);
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);

        return buildUserResponseMap(updatedUser);
    }

    /**
     * Updates account password securely using BCrypt[cite: 2].
     */
    @Transactional
    public void changePassword(Integer userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password provided is incorrect.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Admin Operation: Changes user account status (e.g., ACTIVE, SUSPENDED, REJECTED, PENDING)[cite: 3].
     */
    @Transactional
    public Map<String, Object> updateUserStatus(Integer userId, String newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        String sanitizedStatus = newStatus.toUpperCase().trim();
        if (!List.of("ACTIVE", "SUSPENDED", "REJECTED", "PENDING").contains(sanitizedStatus)) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus);
        }

        user.setStatus(sanitizedStatus);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        return buildUserResponseMap(savedUser);
    }

    /**
     * Admin Operation: Fetches users filtered by specific role ID[cite: 2, 3].
     */
    public List<Map<String, Object>> getUsersByRole(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + roleId));

        List<User> users = userRepository.findByRole(role);
        return users.stream().map(this::buildUserResponseMap).toList();
    }
    
    /**
     * Retrieves list of users matching a given string role name (e.g., AGENT, SELLER).
     */
    public List<Map<String, Object>> getUsersByRole(String roleName) {
        Role role = roleRepository.findByRoleName(roleName.toUpperCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        // Queries Spring Data JPA repository using the Role entity
        List<User> users = userRepository.findByRole(role);
        return users.stream().map(this::buildUserResponseMap).toList();
    }

    /**
     * Internal helper method to package user details into safe response structures.
     */
    private Map<String, Object> buildUserResponseMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("fullName", user.getFullName());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("status", user.getStatus());
        map.put("role", user.getRole() != null ? user.getRole().getRoleName() : null);
        map.put("university", user.getUniversity() != null ? user.getUniversity().getName() : null);
        map.put("referralCodeUsed", user.getReferralCodeUsed());
        map.put("createdAt", user.getCreatedAt());
        map.put("updatedAt", user.getUpdatedAt());
        return map;
    }
}