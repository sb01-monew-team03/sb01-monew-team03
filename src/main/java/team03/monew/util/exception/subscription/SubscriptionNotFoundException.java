package team03.monew.util.exception.subscription;

import java.util.UUID;
import team03.monew.util.exception.ErrorCode;

public class SubscriptionNotFoundException extends SubscriptionException {

  public SubscriptionNotFoundException() {
    super(ErrorCode.SUBSCRIPTION_NOT_FOUND);
  }

  public static SubscriptionNotFoundException withSubscriptionId(UUID subscriptionId) {
    SubscriptionNotFoundException exception = new SubscriptionNotFoundException();
    exception.addDetail("subscriptionId", subscriptionId);
    return exception;
  }
}
