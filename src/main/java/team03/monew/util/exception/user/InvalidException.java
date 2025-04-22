package team03.monew.util.exception.user;

import team03.monew.util.exception.ErrorCode;

public class InvalidException extends UserException {

  public InvalidException() {
    super(ErrorCode.INVALID_USER_CREDENTIALS);
  }

  public static InvalidException wrongPassword() {
    InvalidException exception = new InvalidException();
    return exception;
  }
}
