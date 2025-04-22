package team03.monew.util.exception.user;

import team03.monew.util.exception.ErrorCode;

public class UserAlreadyExistsException extends UserException {
  public UserAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_USER);
  }

  public static UserAlreadyExistsException withEmail(String email) {
    UserAlreadyExistsException exception = new UserAlreadyExistsException();
    exception.addDetail("email", email);
    return exception;
  }
}
