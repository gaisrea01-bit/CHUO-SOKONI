package tz.ac.mzumbe.chuosokoni.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.DisputeService;

@RestController
@RequestMapping("/api/v1/disputes")
@CrossOrigin(origins = "*")
public class DisputeController {

    @Autowired
    private DisputeService disputeService;

    /**
     * Submits a new dispute linked to an order (FR-DSP-001, FR-DSP-002).
     * Expected JSON body: { "orderId", "initiatedByUserId", "reason", "disputeType" (optional) }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDispute(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("orderId") == null || request.get("initiatedByUserId") == null || request.get("reason") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "orderId, initiatedByUserId, and reason are required."));
            }

            Integer orderId = Integer.parseInt(request.get("orderId").toString());
            Integer initiatedByUserId = Integer.parseInt(request.get("initiatedByUserId").toString());
            String reason = (String) request.get("reason");
            String disputeType = (String) request.get("disputeType");

            Map<String, Object> response = disputeService.createDispute(
                    orderId, initiatedByUserId, reason, disputeType
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit dispute: " + e.getMessage()));
        }
    }

    /**
     * Appends text or evidence details to an open dispute (FR-DSP-002).
     * Expected JSON body: { "evidenceDetails" }
     */
    @PostMapping("/{disputeId}/evidence")
    public ResponseEntity<Map<String, Object>> addDisputeEvidence(
            @PathVariable Integer disputeId,
            @RequestBody Map<String, String> request
    ) {
        try {
            String evidenceDetails = request.get("evidenceDetails");
            if (evidenceDetails == null || evidenceDetails.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "evidenceDetails is required."));
            }

            Map<String, Object> response = disputeService.addDisputeEvidence(disputeId, evidenceDetails);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to append evidence: " + e.getMessage()));
        }
    }

    /**
     * Retrieves all open or under-review disputes for the administrative queue (FR-DSP-003, FR-ADM-007).
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingDisputes() {
        try {
            List<Map<String, Object>> disputes = disputeService.getPendingDisputes();
            return ResponseEntity.ok(disputes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Administrative Operation: Reviews, resolves, or rejects a dispute (FR-DSP-003, FR-DSP-004).
     * Expected JSON body: { "adminUserId", "newStatus", "resolution" }
     */
    @PutMapping("/{disputeId}/resolve")
    public ResponseEntity<Map<String, Object>> resolveDispute(
            @PathVariable Integer disputeId,
            @RequestBody Map<String, Object> request
    ) {
        try {
            if (request.get("adminUserId") == null || request.get("newStatus") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "adminUserId and newStatus are required."));
            }

            Integer adminUserId = Integer.parseInt(request.get("adminUserId").toString());
            String newStatus = (String) request.get("newStatus");
            String resolution = (String) request.get("resolution");

            Map<String, Object> response = disputeService.resolveDispute(
                    disputeId, adminUserId, newStatus, resolution
            );

            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to resolve dispute: " + e.getMessage()));
        }
    }
}