package tz.ac.mzumbe.chuosokoni.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.AdminDashboardService;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    /**
     * Retrieves high-level system overview dashboard metrics for administrative users (FR-ADM-005).
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getSystemOverview() {
        try {
            Map<String, Object> overview = adminDashboardService.getSystemOverview();
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve system overview: " + e.getMessage()));
        }
    }

    /**
     * Calculates gross platform revenue and financial metrics across successful payments (FR-ADM-005, FR-ADM-008).
     */
    @GetMapping("/financials")
    public ResponseEntity<Map<String, Object>> getFinancialSummary() {
        try {
            Map<String, Object> financials = adminDashboardService.getFinancialSummary();
            return ResponseEntity.ok(financials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve financial summary: " + e.getMessage()));
        }
    }

    /**
     * Retrieves product review queue and approval status metrics (FR-ADM-002, FR-ADM-003).
     */
    @GetMapping("/product-queue")
    public ResponseEntity<Map<String, Object>> getProductReviewQueueMetrics() {
        try {
            Map<String, Object> queueMetrics = adminDashboardService.getProductReviewQueueMetrics();
            return ResponseEntity.ok(queueMetrics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve product queue metrics: " + e.getMessage()));
        }
    }

    /**
     * Aggregates referral program metrics and agent performance summaries (FR-ADM-006).
     */
    @GetMapping("/referrals")
    public ResponseEntity<Map<String, Object>> getReferralProgramMetrics() {
        try {
            Map<String, Object> referralMetrics = adminDashboardService.getReferralProgramMetrics();
            return ResponseEntity.ok(referralMetrics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve referral metrics: " + e.getMessage()));
        }
    }
}