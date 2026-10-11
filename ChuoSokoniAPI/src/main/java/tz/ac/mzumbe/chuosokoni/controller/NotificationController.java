package tz.ac.mzumbe.chuosokoni.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.NotificationService;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Creates and dispatches a notification to a specific user (FR-4.12).
     * Expected JSON body: { "userId", "type" (optional), "message" }
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendNotification(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("userId") == null || request.get("message") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "userId and message are required."));
            }

            Integer userId = Integer.parseInt(request.get("userId").toString());
            String type = (String) request.get("type");
            String message = (String) request.get("message");

            Map<String, Object> response = notificationService.sendNotification(userId, type, message);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send notification: " + e.getMessage()));
        }
    }

    /**
     * Retrieves all notifications for a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserNotifications(@PathVariable Integer userId) {
        try {
            List<Map<String, Object>> notifications = notificationService.getUserNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves only unread notifications for a user.
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Map<String, Object>>> getUnreadUserNotifications(@PathVariable Integer userId) {
        try {
            List<Map<String, Object>> notifications = notificationService.getUnreadUserNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Marks a specific notification as read.
     * Expected JSON body: { "userId" }
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable Integer notificationId,
            @RequestBody Map<String, Object> request
    ) {
        try {
            if (request.get("userId") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "userId is required to mark notification as read."));
            }

            Integer userId = Integer.parseInt(request.get("userId").toString());
            Map<String, Object> response = notificationService.markAsRead(notificationId, userId);

            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to mark notification as read: " + e.getMessage()));
        }
    }

    /**
     * Marks all unread notifications as read for a user.
     * Expected JSON body: { "userId" }
     */
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("userId") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "userId is required."));
            }

            Integer userId = Integer.parseInt(request.get("userId").toString());
            notificationService.markAllAsRead(userId);

            return ResponseEntity.ok(Map.of("message", "All unread notifications marked as read successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to mark all notifications as read: " + e.getMessage()));
        }
    }
}