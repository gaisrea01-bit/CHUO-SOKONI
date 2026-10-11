package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Notification;
import tz.ac.mzumbe.chuosokoni.model.User;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Integer> {

    List<Notification> findByUser_UserId(Integer userId);

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

    List<Notification> findByUser_UserIdAndIsRead(
            Integer userId,
            Boolean isRead
    );

    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(
            Integer userId
    );

    long countByUser_UserIdAndIsRead(
            Integer userId,
            Boolean isRead
    );
}
