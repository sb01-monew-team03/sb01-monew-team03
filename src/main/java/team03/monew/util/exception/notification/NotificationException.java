package team03.monew.util.exception.notification;

import team03.monew.util.exception.ErrorCode;
import team03.monew.util.exception.MonewException;

public class NotificationException extends MonewException {

  public NotificationException(ErrorCode errorCode) {
    super(errorCode);
  }

  public NotificationException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }

}
