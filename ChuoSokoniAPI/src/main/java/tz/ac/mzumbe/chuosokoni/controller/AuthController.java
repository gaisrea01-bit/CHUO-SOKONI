package tz.ac.mzumbe.chuosokoni.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Registers a new user account (Customer, Agent, Seller, or Investor).
     * Expected JSON body: { "fullName", "email", "password", "phoneNumber", "universityId", "roleName", "referralCode" (optional) }
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> registerRequest) {
        try {
            String fullName = (String) registerRequest.get("fullName");
            String email = (String) registerRequest.get("email");
            String password = (String) registerRequest.get("password");
            String phoneNumber = (String) registerRequest.get("phoneNumber");
            Integer universityId = registerRequest.get("universityId") != null ? 
                    Integer.parseInt(registerRequest.get("universityId").toString()) : null;
            String roleName = (String) registerRequest.get("roleName");
            String referralCode = (String) registerRequest.get("referralCode");

            Map<String, Object> response = authService.registerUser(
                    fullName, email, password, phoneNumber, universityId, roleName, referralCode
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred during registration: " + e.getMessage()));
        }
    }

    /**
     * Authenticates a user and generates a JWT access token.
     * Expected JSON body: { "email", "password" }
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> loginRequest) {
        try {
            String email = (String) loginRequest.get("email");
            String password = (String) loginRequest.get("password");

            Map<String, Object> response = authService.authenticateUser(email, password);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred during authentication: " + e.getMessage()));
        }
    }

    /**
     * Validates and refreshes an expired JWT access token.
     * Expected JSON body: { "refreshToken" }
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> refreshRequest) {
        try {
            String token = refreshRequest.get("refreshToken");
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Refresh token is required."));
            }

            Map<String, Object> response = authService.refreshToken(token);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Token refresh failed: " + e.getMessage()));
        }
    }
}