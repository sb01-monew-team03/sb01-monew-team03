package team03.monew.util.exception.subscription;

import java.util.UUID;
import team03.monew.util.exception.ErrorCode;

public class SubscriptionAlreadyExistException extends SubscriptionException {

  public SubscriptionAlreadyExistException() {
    super(ErrorCode.DUPLICATE_SUBSCRIPTION);
  }

  public static SubscriptionAlreadyExistException withInterestIdAndUserId(UUID userId,
      UUID interestId) {
    SubscriptionAlreadyExistException exception = new SubscriptionAlreadyExistException();
    exception.addDetail("userId", userId);
    exception.addDetail("interestId", interestId);
    return exception;
  }
}
