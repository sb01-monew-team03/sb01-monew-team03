package team03.monew.util.exception.notification;

import java.util.UUID;
import team03.monew.util.exception.ErrorCode;

public class NotificationNotFoundException extends NotificationException {

  public NotificationNotFoundException() {
    super(ErrorCode.NOTIFICATION_NOT_FOUND);
  }

  public static NotificationException withId(UUID id) {
    NotificationException exception = new NotificationNotFoundException();
    exception.addDetail("notificationId", id);
    return exception;
  }
}
