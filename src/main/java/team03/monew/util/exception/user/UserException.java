package team03.monew.util.exception.user;

import team03.monew.util.exception.ErrorCode;
import team03.monew.util.exception.MonewException;

public class UserException extends MonewException {

  public UserException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
