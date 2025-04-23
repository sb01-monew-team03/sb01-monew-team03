package team03.monew.util.exception.comment;

import java.util.UUID;
import team03.monew.util.exception.ErrorCode;

public class CommentNotFoundException extends CommentException {

  public CommentNotFoundException() {
    super(ErrorCode.COMMENT_NOT_FOUND);
  }

  public static CommentNotFoundException withId(UUID commentId) {
    CommentNotFoundException exception = new CommentNotFoundException();
    exception.addDetail("commentId", commentId);
    return exception;
  }

}
