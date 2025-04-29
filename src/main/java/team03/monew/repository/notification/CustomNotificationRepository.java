package team03.monew.repository.notification;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team03.monew.entity.notification.Notification;

public interface CustomNotificationRepository {

  void confirmAllByUserId(UUID userId);

  void deleteAllConfirmNotification(Instant time);

  Page<Notification> findPageWithCursor(UUID userId, String cursor, Pageable pageable);

}
