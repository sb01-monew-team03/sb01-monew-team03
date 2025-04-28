package team03.monew.util.exception.interest;

import team03.monew.util.exception.ErrorCode;

public class UnauthorizedException extends InterestException{

  public UnauthorizedException() {
    super(ErrorCode.UNAUTHORIZED);
  }

  public static UnauthorizedException withRole(String role) {
    UnauthorizedException exception = new UnauthorizedException();
    exception.addDetail("role", role);
    return exception;
  }
}
