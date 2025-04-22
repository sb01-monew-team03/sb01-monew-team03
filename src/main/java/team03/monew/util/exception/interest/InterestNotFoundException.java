package team03.monew.util.exception.interest;

import java.util.UUID;
import team03.monew.util.exception.ErrorCode;

public class InterestNotFoundException extends InterestException {

  public InterestNotFoundException() {
    super(ErrorCode.INTEREST_NOT_FOUND);
  }

  public static InterestNotFoundException withInterestId(UUID interestId) {
    InterestNotFoundException exception = new InterestNotFoundException();
    exception.addDetail("interestId", interestId);
    return exception;
  }
}
