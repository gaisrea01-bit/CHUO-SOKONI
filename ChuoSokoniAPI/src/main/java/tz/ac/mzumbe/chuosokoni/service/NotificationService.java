package tz.ac.mzumbe.chuosokoni.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Notification;
import tz.ac.mzumbe.chuosokoni.model.User;
import tz.ac.mzumbe.chuosokoni.repository.NotificationRepository;
import tz.ac.mzumbe.chuosokoni.repository.UserRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Creates and dispatches a notification to a specific user (FR-4.12)[cite: 2, 3].
     */
    @Transactional
    public Map<String, Object> sendNotification(
            Integer userId,
            String type,
            String message
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification message content cannot be empty.");
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type != null ? type.toUpperCase().trim() : "GENERAL");
        notification.setMessage(message.trim());
        notification.setIsRead(false);

        Notification savedNotification = notificationRepository.save(notification);
        return buildNotificationResponseMap(savedNotification);
    }

    /**
     * Retrieves all notifications for a specific user.
     */
    public List<Map<String, Object>> getUserNotifications(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        return notifications.stream().map(this::buildNotificationResponseMap).toList();
    }

    /**
     * Retrieves only unread notifications for a user[cite: 2].
     */
    public List<Map<String, Object>> getUnreadUserNotifications(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        List<Notification> notifications = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
        return notifications.stream().map(this::buildNotificationResponseMap).toList();
    }

    /**
     * Marks a specific notification as read[cite: 2].
     */
    @Transactional
    public Map<String, Object> markAsRead(Integer notificationId, Integer userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized action. Notification does not belong to user.");
        }

        notification.setIsRead(true);
        Notification savedNotification = notificationRepository.save(notification);
        return buildNotificationResponseMap(savedNotification);
    }

    /**
     * Marks all unread notifications as read for a user[cite: 2].
     */
    @Transactional
    public void markAllAsRead(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    /**
     * Helper to package notification outputs into response maps.
     */
    private Map<String, Object> buildNotificationResponseMap(Notification notification) {
        Map<String, Object> map = new HashMap<>();
        map.put("notificationId", notification.getNotificationId());
        map.put("userId", notification.getUser().getUserId());
        map.put("type", notification.getType());
        map.put("message", notification.getMessage());
        map.put("isRead", notification.getIsRead());
        map.put("createdAt", notification.getCreatedAt());
        return map;
    }
}