package team03.monew.repository.notification;

import java.time.Instant;
import java.util.UUID;

public interface CustomNotificationRepository {

  void confirmAllByUserId(UUID userId);

  void deleteAllConfirmNotification(Instant time);

}
