package team03.monew.util.exception.user;

import team03.monew.util.exception.ErrorCode;

public class ForbiddenException extends UserException {

  public ForbiddenException() {
    super(ErrorCode.FORBIDDEN_REQUEST);
  }

  public static ForbiddenException WrongUserId() {
    ForbiddenException exception = new ForbiddenException();
    return exception;
  }
}
