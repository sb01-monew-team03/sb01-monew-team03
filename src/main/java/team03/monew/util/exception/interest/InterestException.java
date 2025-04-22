package team03.monew.util.exception.interest;

import team03.monew.util.exception.ErrorCode;
import team03.monew.util.exception.MonewException;

public class InterestException extends MonewException {

  public InterestException(ErrorCode errorCode) {
    super(errorCode);
  }

  public InterestException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
