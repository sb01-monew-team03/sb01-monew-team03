package team03.monew.util.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
  private final Instant timestamp;
  private final String code;
  private final String message;
  private final Map<String, Object> details;
  private final String exceptionType;
  private final int status;

  public ErrorResponse(MonewException exception) {
    this(Instant.now(),
        exception.getErrorCode().name(),
        exception.getMessage(),
        exception.getDetails(),
        exception.getClass().getSimpleName(),
        exception.getErrorCode().getHttpStatus().value());
  }

  public ErrorResponse(Exception exception) {
    this(Instant.now(),
        exception.getClass().getSimpleName(),
        exception.getMessage(),
        new HashMap<>(),
        exception.getClass().getSimpleName(),
        HttpStatus.INTERNAL_SERVER_ERROR.value());
  }
}