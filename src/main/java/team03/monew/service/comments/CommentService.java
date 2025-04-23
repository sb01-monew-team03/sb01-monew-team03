package team03.monew.service.comments;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.dto.comments.CommentCreateRequest;
import team03.monew.dto.comments.CommentDto;
import team03.monew.dto.comments.CommentLikeDto;
import team03.monew.dto.comments.CommentUpdateRequest;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.comments.CommentLike;
import team03.monew.entity.article.Article;
import team03.monew.entity.user.User;
import team03.monew.mapper.comments.CommentMapper;
import team03.monew.repository.comments.CommentLikeRepository;
import team03.monew.repository.comments.CommentRepository;
import team03.monew.repository.article.ArticleRepository;
import team03.monew.repository.user.UserRepository;
import team03.monew.util.exception.article.ArticleNotFoundException;
import team03.monew.util.exception.comments.AlreadyLikedException;
import team03.monew.util.exception.comments.CommentNotFoundException;
import team03.monew.util.exception.comments.LikeNotFoundException;
import team03.monew.util.exception.user.UserNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentMapper commentMapper;

    // 1. 댓글 등록
    public CommentDto create(CommentCreateRequest request) {
        log.debug("댓글 등록 시작: articleId={}, userId={}", request.articleId(), request.userId());
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> UserNotFoundException.withId(request.userId()));
        Article article = articleRepository.findById(request.articleId())
                .orElseThrow(() -> new ArticleNotFoundException(request.articleId()));

        Comment comment = new Comment(request.content(), user, article);
        Comment saved = commentRepository.save(comment);
        log.info("댓글 등록 완료: commentId={}", saved.getId());
        return commentMapper.toDto(saved);
    }

    // 2. 댓글 목록 조회
    @Transactional(readOnly = true)
    public Page<CommentDto> listByArticle(UUID articleId,
                                          String orderBy,
                                          Sort.Direction direction,
                                          int limit,
                                          UUID requesterId) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(direction, orderBy));
        Page<Comment> page = commentRepository.findByArticleIdAndDeletedAtIsNull(articleId, pageRequest);

        return page.map(cmt -> {
            boolean likedByMe = commentLikeRepository.existsByCommentIdAndUserId(cmt.getId(), requesterId);
            return commentMapper.toDtoWithLike(cmt, likedByMe);
        });
    }

    // 3. 댓글 수정
    public CommentDto update(UUID commentId, UUID userId, CommentUpdateRequest request) {
        log.debug("댓글 수정 시작: commentId={}", commentId);
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> CommentNotFoundException.withId(commentId));
        comment.update(request.content());
        log.info("댓글 수정 완료: commentId={}", comment.getId());
        boolean likedByMe = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        return commentMapper.toDtoWithLike(comment, likedByMe);
    }

    // 4. 댓글 논리 삭제
    public void softDelete(UUID commentId, UUID userId) {
        log.debug("댓글 논리 삭제 시작: commentId={}", commentId);
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> CommentNotFoundException.withId(commentId));
        comment.delete();
        log.info("댓글 논리 삭제 완료: commentId={}", commentId);
    }

    // 5. 댓글 물리 삭제
    public void hardDelete(UUID commentId, UUID userId) {
        log.debug("댓글 물리 삭제 시작: commentId={}", commentId);
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> CommentNotFoundException.withId(commentId));
        commentRepository.delete(comment);
        log.info("댓글 물리 삭제 완료: commentId={}", commentId);
    }

    // 6. 댓글 좋아요 등록
    public CommentLikeDto likeComment(UUID commentId, UUID userId) {
        log.debug("댓글 좋아요 등록 시작: commentId={}, userId={}", commentId, userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> CommentNotFoundException.withId(commentId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
        if (commentLikeRepository.existsByCommentAndUser(comment, user)) {
            throw new AlreadyLikedException(commentId, userId);
        }
        CommentLike like = new CommentLike(comment, user, comment.getArticle());
        commentLikeRepository.save(like);
        comment.increaseLikeCount();
        log.info("댓글 좋아요 등록 완료: likeId={}", like.getId());
        return commentMapper.toLikeDto(like); //'CommentMapper'의 메서드 'toLikeDto'을(를) 해결할 수 없습니다
    }

    // 7. 댓글 좋아요 취소
    public void unlikeComment(UUID commentId, UUID userId) {
        log.debug("댓글 좋아요 취소 시작: commentId={}, userId={}", commentId, userId);
        CommentLike like = commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> LikeNotFoundException.forUserAndComment(userId, commentId));
        like.getComment().decreaseLikeCount();
        commentLikeRepository.delete(like);
        log.info("댓글 좋아요 취소 완료: commentId={}, userId={}", commentId, userId);
    }
}