package tz.ac.mzumbe.chuosokoni.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Retrieves a user's full profile details by user ID.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Integer userId) {
        try {
            Map<String, Object> response = userService.getUserProfile(userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve user profile: " + e.getMessage()));
        }
    }

    /**
     * Updates profile details for a user account.
     * Expected JSON body: { "fullName", "phoneNumber", "universityId" }
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @PathVariable Integer userId,
            @RequestBody Map<String, Object> updateRequest
    ) {
        try {
            String fullName = (String) updateRequest.get("fullName");
            String phoneNumber = (String) updateRequest.get("phoneNumber");
            Integer universityId = updateRequest.get("universityId") != null ?
                    Integer.parseInt(updateRequest.get("universityId").toString()) : null;

            Map<String, Object> response = userService.updateUserProfile(userId, fullName, phoneNumber, universityId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user profile: " + e.getMessage()));
        }
    }

    /**
     * Admin Operation: Updates account status (e.g., ACTIVE, SUSPENDED, DEACTIVATED).
     * Expected JSON body: { "status" }
     */
    @PutMapping("/{userId}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> statusRequest
    ) {
        try {
            String status = statusRequest.get("status");
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Status field is required."));
            }

            Map<String, Object> response = userService.updateUserStatus(userId, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update account status: " + e.getMessage()));
        }
    }

    /**
     * Admin Operation: Lists users filtered by role name (e.g., CUSTOMER, SELLER, AGENT, INVESTOR).
     */
    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<Map<String, Object>>> getUsersByRole(@PathVariable String roleName) {
        try {
            List<Map<String, Object>> users = userService.getUsersByRole(roleName);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}