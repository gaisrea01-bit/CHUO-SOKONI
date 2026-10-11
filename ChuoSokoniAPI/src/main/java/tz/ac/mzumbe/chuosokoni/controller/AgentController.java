package tz.ac.mzumbe.chuosokoni.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.AgentService;

@RestController
@RequestMapping("/api/v1/agents")
@CrossOrigin(origins = "*")
public class AgentController {

    @Autowired
    private AgentService agentService;

    /**
     * Provisions or upgrades a user account to an Agent profile.
     * Expected JSON body: { "userId": 12 }
     */
    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> createAgentProfile(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("userId") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "userId field is required."));
            }

            Integer userId = Integer.parseInt(request.get("userId").toString());
            Map<String, Object> response = agentService.createAgentProfile(userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create agent profile: " + e.getMessage()));
        }
    }

    /**
     * Retrieves an agent's dashboard metrics (referral code, total referrals, level, commission earned).
     */
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<Map<String, Object>> getAgentDashboard(@PathVariable Integer userId) {
        try {
            Map<String, Object> response = agentService.getAgentDashboard(userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch agent dashboard: " + e.getMessage()));
        }
    }

    /**
     * Fetches all users onboarded via a specific agent's referral code.
     */
    @GetMapping("/{userId}/referred-users")
    public ResponseEntity<List<Map<String, Object>>> getReferredUsers(@PathVariable Integer userId) {
        try {
            List<Map<String, Object>> referredUsers = agentService.getReferredUsers(userId);
            return ResponseEntity.ok(referredUsers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}