package team03.monew.util.exception.subscription;

import team03.monew.util.exception.ErrorCode;
import team03.monew.util.exception.MonewException;

public class SubscriptionException extends MonewException {

  public SubscriptionException(ErrorCode errorCode) {
    super(errorCode);
  }

  public SubscriptionException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
