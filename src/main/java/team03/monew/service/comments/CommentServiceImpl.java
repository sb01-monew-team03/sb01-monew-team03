package team03.monew.service.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.dto.comments.*;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.comments.CommentLike;
import team03.monew.entity.article.Article;
import team03.monew.entity.user.User;
import team03.monew.mapper.comments.CommentMapper;
import team03.monew.repository.article.ArticleRepository;
import team03.monew.repository.comments.CommentLikeRepository;
import team03.monew.repository.comments.CommentRepository;
import team03.monew.repository.user.UserRepository;
import team03.monew.util.exception.comments.*;
import team03.monew.util.exception.user.UserNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto create(CommentCreateRequest request) {
        log.debug("댓글 등록 시작: articleId={}, userId={}", request.articleId(), request.userId());
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> UserNotFoundException.withId(request.userId()));
        Article article = articleRepository.findById(request.articleId())
            .orElseThrow(() -> new RuntimeException("Article not found: " + request.articleId()));

        Comment comment = new Comment(request.content(), user, article);
        Comment saved = commentRepository.save(comment);
        log.info("댓글 등록 완료: commentId={}", saved.getId());

        boolean likedByMe = commentLikeRepository.existsByCommentIdAndUserId(saved.getId(), user.getId());
        return commentMapper.toDto(saved, likedByMe);
    }

    @Override
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
            return commentMapper.toDto(cmt, likedByMe);
        });
    }

    @Override
    public CommentDto update(UUID commentId, UUID userId, CommentUpdateRequest request) {
        log.debug("댓글 수정 시작: commentId={}", commentId);
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> CommentNotFoundException.withId(commentId));
        comment.update(request.content());
        log.info("댓글 수정 완료: commentId={}", comment.getId());
        boolean likedByMe = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        return commentMapper.toDto(comment, likedByMe);
    }

    @Override
    public void softDelete(UUID commentId, UUID userId) {
        log.debug("댓글 논리 삭제 시작: commentId={}", commentId);
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> CommentNotFoundException.withId(commentId));
        comment.delete();
        log.info("댓글 논리 삭제 완료: commentId={}", commentId);
    }

    @Override
    public void hardDelete(UUID commentId, UUID userId) {
        log.debug("댓글 물리 삭제 시작: commentId={}", commentId);
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> CommentNotFoundException.withId(commentId));
        commentRepository.delete(comment);
        log.info("댓글 물리 삭제 완료: commentId={}", commentId);
    }

    @Override
    public CommentLikeDto likeComment(UUID commentId, UUID userId) {
        log.debug("댓글 좋아요 등록 시작: commentId={}, userId={}", commentId, userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> CommentNotFoundException.withId(commentId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
        if (commentLikeRepository.existsByCommentAndUser(comment, user)) {
            throw new AlreadyLikedException(commentId, userId);
        }
        CommentLike newLike = new CommentLike(comment, user, comment.getArticle());
        CommentLike savedLike = commentLikeRepository.save(newLike);
        comment.increaseLikeCount();
        log.info("댓글 좋아요 등록 완료: likeId={}", savedLike.getId());
        return commentMapper.toLikeDto(savedLike);
    }

    @Override
    public void unlikeComment(UUID commentId, UUID userId) {
        log.debug("댓글 좋아요 취소 시작: commentId={}, userId={}", commentId, userId);
        CommentLike like = commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> LikeNotFoundException.forUserAndComment(userId, commentId));
        Comment target = like.getComment();
        if (target != null) {
            target.decreaseLikeCount();
        }
        commentLikeRepository.delete(like);
        log.info("댓글 좋아요 취소 완료: commentId={}, userId={}", commentId, userId);
    }

    @Override
    public CursorPageResponse<CommentDto> listByArticleCursor(
            UUID articleId,
            String orderBy,
            Sort.Direction direction,
            int limit,
            String cursor,
            Instant after,
            UUID requesterId
    ) {
        log.debug("댓글 커서 리스트 조회 시작: articleId={}, orderBy={}, direction={}, limit={}, cursor={}, after={}, requesterId={}",
                articleId, orderBy, direction, limit, cursor, after, requesterId);

        Specification<Comment> spec = (root, query, cb) -> {
            var p = cb.conjunction();
            p = cb.and(p,
                    cb.equal(root.get("article").get("id"), articleId),
                    cb.isNull(root.get("deletedAt"))
            );

            if (cursor != null) {
                if ("createdAt".equals(orderBy)) {
                    Instant cur = Instant.parse(cursor);
                    p = cb.and(p, direction.isDescending()
                            ? cb.lessThan(root.get("createdAt"), cur)
                            : cb.greaterThan(root.get("createdAt"), cur));
                } else {
                    long curLike = Long.parseLong(cursor);
                    var likeExp = root.get("likeCount").as(Long.class);
                    var tie = cb.equal(likeExp, curLike);
                    var timeCond = direction.isDescending()
                            ? cb.lessThan(root.get("createdAt"), after)
                            : cb.greaterThan(root.get("createdAt"), after);
                    p = cb.and(p, cb.or(cb.lessThanOrEqualTo(likeExp, curLike), cb.and(tie, timeCond)));
                }
            }
            return p;
        };

        Sort sort = Sort.by(direction, orderBy).and(Sort.by(direction, "createdAt"));
        Pageable pageReq = PageRequest.of(0, limit, sort);
        Page<Comment> page = commentRepository.findAll(spec, pageReq);

        List<CommentDto> dtos = page.getContent().stream()
                .map(c -> {
                    boolean liked = commentLikeRepository.existsByCommentIdAndUserId(c.getId(), requesterId);
                    return commentMapper.toDto(c, liked);
                })
                .collect(Collectors.toList());

        String nextCursor = null;
        Instant nextAfter = null;
        if (page.hasNext() && !dtos.isEmpty()) {
            Comment last = page.getContent().get(page.getSize() - 1);
            nextCursor = "createdAt".equals(orderBy)
                    ? last.getCreatedAt().toString()
                    : Long.toString(last.getLikeCount());
            nextAfter = last.getCreatedAt();
        }

        CursorPageResponse<CommentDto> resp = new CursorPageResponse<>(
                dtos, nextCursor, nextAfter, dtos.size(), page.getTotalElements(), page.hasNext()
        );

        log.info("댓글 커서 리스트 조회 완료: size={}, totalElements={}, hasNext={}",
                resp.size(), resp.totalElements(), resp.hasNext());
        return resp;
    }
}
