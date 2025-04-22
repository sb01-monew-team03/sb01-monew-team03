package team03.monew.util.exception.comments;

import java.util.UUID;

public class CommentNotFoundException extends RuntimeException {

    private CommentNotFoundException(String message) {
        super(message);
    }

    public static CommentNotFoundException withId(UUID id) {
        return new CommentNotFoundException("댓글을 찾을 수 없습니다. commentId: " + id);
    }

    public static CommentNotFoundException withArticleId(UUID articleId) {
        return new CommentNotFoundException("해당 게시글에 대한 댓글이 존재하지 않습니다. articleId: " + articleId);
    }
}
