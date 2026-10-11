package tz.ac.mzumbe.chuosokoni.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.SystemLog;
import tz.ac.mzumbe.chuosokoni.model.User;
import tz.ac.mzumbe.chuosokoni.repository.SystemLogRepository;
import tz.ac.mzumbe.chuosokoni.repository.UserRepository;

@Service
public class AuditLogService {

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Records a system or administrative event into system_logs (FR-ADM-009, NFR-005).
     */
    @Transactional
    public Map<String, Object> logEvent(
            Integer userId,
            String action,
            String module,
            String details,
            String ipAddress
    ) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        SystemLog log = new SystemLog();
        log.setUser(user);
        log.setAction(action != null ? action.toUpperCase().trim() : "UNKNOWN_ACTION");
        log.setModule(module != null ? module.toUpperCase().trim() : "GENERAL");
        log.setDetails(details);
        log.setIpAddress(ipAddress != null ? ipAddress : "0.0.0.0");

        SystemLog savedLog = systemLogRepository.save(log);
        return buildLogResponseMap(savedLog);
    }

    /**
     * Retrieves audit logs associated with a specific system module (e.g., USER_MANAGEMENT, PAYMENTS, DISPUTES).
     */
    public List<Map<String, Object>> getLogsByModule(String module) {
        List<SystemLog> logs = systemLogRepository.findByModuleOrderByCreatedAtDesc(module.toUpperCase().trim());
        return logs.stream().map(this::buildLogResponseMap).toList();
    }

    /**
     * Retrieves recent audit logs for administrative review (FR-ADM-009)[cite: 3].
     */
    public List<Map<String, Object>> getRecentAuditLogs() {
        List<SystemLog> logs = systemLogRepository.findTop100ByOrderByCreatedAtDesc();
        return logs.stream().map(this::buildLogResponseMap).toList();
    }

    /**
     * Helper to format system log records into response maps.
     */
    private Map<String, Object> buildLogResponseMap(SystemLog log) {
        Map<String, Object> map = new HashMap<>();
        map.put("logId", log.getLogId());
        map.put("userId", log.getUser() != null ? log.getUser().getUserId() : null);
        map.put("userName", log.getUser() != null ? log.getUser().getFullName() : "SYSTEM");
        map.put("action", log.getAction());
        map.put("module", log.getModule());
        map.put("details", log.getDetails());
        map.put("ipAddress", log.getIpAddress());
        map.put("timestamp", log.getCreatedAt());
        return map;
    }
}