package team03.monew.repository.notification;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import team03.monew.entity.notification.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID>,
    CustomNotificationRepository {

}
