package uk.ac.sheffield.com2008_team_27.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import uk.ac.sheffield.com2008_team_27.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUserId(Integer userId);
    List<Notification> findAllByReadAndUserId(boolean read, Integer userId);
}
