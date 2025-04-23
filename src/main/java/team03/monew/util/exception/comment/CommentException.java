package team03.monew.util.exception.comment;

import team03.monew.util.exception.ErrorCode;
import team03.monew.util.exception.MonewException;

public class CommentException extends MonewException {

  public CommentException(ErrorCode errorCode) {
    super(errorCode);
  }

  public CommentException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
