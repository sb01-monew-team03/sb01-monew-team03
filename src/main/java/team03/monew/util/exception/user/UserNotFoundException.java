package team03.monew.util.exception.user;

import java.util.UUID;
import team03.monew.util.exception.ErrorCode;

public class UserNotFoundException extends UserException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }

  public static UserNotFoundException withId(UUID userId) {
    UserNotFoundException exception = new UserNotFoundException();
    exception.addDetail("userId", userId);
    return exception;
  }

  public static UserNotFoundException withEmail(String email) {
    UserNotFoundException exception = new UserNotFoundException();
    exception.addDetail("email", email);
    return exception;
  }

  public static UserNotFoundException isDeleted() {
    UserNotFoundException exception = new UserNotFoundException();
    return exception;
  }
}
