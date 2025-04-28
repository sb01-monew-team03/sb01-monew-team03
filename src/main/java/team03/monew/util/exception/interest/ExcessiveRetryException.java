package team03.monew.util.exception.interest;

import team03.monew.util.exception.ErrorCode;

public class ExcessiveRetryException extends InterestException{

  public ExcessiveRetryException() {
    super(ErrorCode.ERROR_MAX_RETRY_EXCEEDED);
  }
}
