package team03.monew.util.exception.comments;

import java.util.UUID;
import team03.monew.util.exception.MonewException;
import team03.monew.util.exception.ErrorCode;


public class CommentNotFoundException extends MonewException {

    private CommentNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static CommentNotFoundException withId(UUID id) {
        CommentNotFoundException ex = new CommentNotFoundException(ErrorCode.COMMENT_NOT_FOUND);
        ex.addDetail("commentId", id);
        return ex;
    }

    public static CommentNotFoundException withArticleId(UUID articleId) {
        CommentNotFoundException ex = new CommentNotFoundException(ErrorCode.COMMENT_NOT_FOUND_BY_ARTICLE);
        ex.addDetail("articleId", articleId);
        return ex;
    }
}