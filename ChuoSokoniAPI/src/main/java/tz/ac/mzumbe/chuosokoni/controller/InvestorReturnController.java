package tz.ac.mzumbe.chuosokoni.controller;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.InvestorReturnService;

@RestController
@RequestMapping("/api/v1/investors")
@CrossOrigin(origins = "*")
public class InvestorReturnController {

    @Autowired
    private InvestorReturnService investorReturnService;

    /**
     * Fetches investor portfolio summary including available capital, risk preference, and investment type.
     */
    @GetMapping("/portfolio/{investorUserId}")
    public ResponseEntity<Map<String, Object>> getInvestorPortfolioSummary(@PathVariable Integer investorUserId) {
        try {
            Map<String, Object> response = investorReturnService.getInvestorPortfolioSummary(investorUserId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve portfolio summary: " + e.getMessage()));
        }
    }

    /**
     * Credits an investor return share (35% of commercial margin) after a qualifying wholesale product resale.
     * Expected JSON body: { "investorUserId", "orderId", "returnAmount" }
     */
    @PostMapping("/credit-return")
    public ResponseEntity<Map<String, Object>> creditInvestorReturn(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("investorUserId") == null || request.get("orderId") == null || request.get("returnAmount") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "investorUserId, orderId, and returnAmount are required."));
            }

            Integer investorUserId = Integer.parseInt(request.get("investorUserId").toString());
            Integer orderId = Integer.parseInt(request.get("orderId").toString());
            BigDecimal returnAmount = new BigDecimal(request.get("returnAmount").toString());

            Map<String, Object> response = investorReturnService.creditInvestorReturn(investorUserId, orderId, returnAmount);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to credit investor return: " + e.getMessage()));
        }
    }

    /**
     * Processes an investor capital withdrawal request.
     * Expected JSON body: { "investorUserId", "withdrawalAmount" }
     */
    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> requestCapitalWithdrawal(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("investorUserId") == null || request.get("withdrawalAmount") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "investorUserId and withdrawalAmount are required."));
            }

            Integer investorUserId = Integer.parseInt(request.get("investorUserId").toString());
            BigDecimal withdrawalAmount = new BigDecimal(request.get("withdrawalAmount").toString());

            Map<String, Object> response = investorReturnService.requestCapitalWithdrawal(investorUserId, withdrawalAmount);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process withdrawal request: " + e.getMessage()));
        }
    }
}