package tz.ac.mzumbe.chuosokoni.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.CommissionService;

@RestController
@RequestMapping("/api/v1/commissions")
@CrossOrigin(origins = "*")
public class CommissionController {

    @Autowired
    private CommissionService commissionService;

    /**
     * Records a qualifying agent commission after a verified sale (FR-AGT-004).
     * Expected JSON body: { "agentId", "orderId", "commissionAmount", "transactionType" (optional) }
     */
    @PostMapping("/record")
    public ResponseEntity<Map<String, Object>> recordCommission(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("agentId") == null || request.get("orderId") == null || request.get("commissionAmount") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "agentId, orderId, and commissionAmount are required."));
            }

            Integer agentId = Integer.parseInt(request.get("agentId").toString());
            Integer orderId = Integer.parseInt(request.get("orderId").toString());
            BigDecimal commissionAmount = new BigDecimal(request.get("commissionAmount").toString());
            String transactionType = (String) request.get("transactionType");

            Map<String, Object> response = commissionService.recordCommission(
                    agentId, orderId, commissionAmount, transactionType
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to record commission: " + e.getMessage()));
        }
    }

    /**
     * Administrative Operation: Reverses a previously awarded commission for order refunds or disputes.
     * Expected JSON body: { "reason" (optional) }
     */
    @PostMapping("/{commissionId}/reverse")
    public ResponseEntity<Map<String, Object>> reverseCommission(
            @PathVariable Integer commissionId,
            @RequestBody(required = false) Map<String, String> request
    ) {
        try {
            String reason = (request != null) ? request.get("reason") : null;

            Map<String, Object> response = commissionService.reverseCommission(commissionId, reason);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reverse commission: " + e.getMessage()));
        }
    }

    /**
     * Retrieves commission ledger history for an individual agent.
     */
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Map<String, Object>>> getAgentCommissions(@PathVariable Integer agentId) {
        try {
            List<Map<String, Object>> commissions = commissionService.getAgentCommissions(agentId);
            return ResponseEntity.ok(commissions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}