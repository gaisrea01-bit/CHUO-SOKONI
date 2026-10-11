package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.SystemLog;

@Repository
public interface SystemLogRepository
        extends JpaRepository<SystemLog, Integer> {

    List<SystemLog> findByUser_UserId(Integer userId);

    List<SystemLog> findByModuleOrderByCreatedAtDesc(String module);

    List<SystemLog> findTop100ByOrderByCreatedAtDesc();

    List<SystemLog> findByModule(String module);

    List<SystemLog> findByAction(String action);

    List<SystemLog> findByIpAddress(String ipAddress);

    List<SystemLog> findByUser_UserIdOrderByCreatedAtDesc(
            Integer userId
    );
}
