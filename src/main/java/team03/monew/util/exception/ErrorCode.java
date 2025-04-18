package team03.monew.util.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  // user 에러 코드
  USER_NOT_FOUND("USER_001", "User not found", HttpStatus.NOT_FOUND),
  INVALID_USER_CREDENTIALS("USER_002", "Wrong password", HttpStatus.UNAUTHORIZED),
  DUPLICATE_USER("USER_002", "Duplicate user", HttpStatus.BAD_REQUEST),

  // Server 에러 코드
  INTERNAL_SERVER_ERROR("SERVER_001","서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_REQUEST("SERVER+002", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(String code, String message, HttpStatus httpStatus) {
    this.code = code;
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
