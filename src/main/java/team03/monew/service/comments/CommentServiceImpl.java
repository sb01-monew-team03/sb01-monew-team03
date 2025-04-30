package team03.monew.service.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.dto.comments.CommentCreateRequest;
import team03.monew.dto.comments.CommentDto;
import team03.monew.dto.comments.CommentLikeDto;
import team03.monew.dto.comments.CommentUpdateRequest;
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
import team03.monew.util.exception.comments.AlreadyLikedException;
import team03.monew.util.exception.comments.CommentNotFoundException;
import team03.monew.util.exception.comments.LikeNotFoundException;
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
        // ArticleNotFoundException 임시 미존재 처리
        Article article = articleRepository.findById(request.articleId())
                .orElseThrow(() -> new RuntimeException("Article not found: " + request.articleId()));

        Comment comment = new Comment(request.content(), user, article);
        Comment saved = commentRepository.save(comment);
        log.info("댓글 등록 완료: commentId={}", saved.getId());
        return commentMapper.toDto(saved);
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
        CommentLike like = new CommentLike(comment, user, comment.getArticle());
        commentLikeRepository.save(like);
        comment.increaseLikeCount();
        log.info("댓글 좋아요 등록 완료: likeId={}", like.getId());
        return commentMapper.toLikeDto(like);
    }

    @Override
    public void unlikeComment(UUID commentId, UUID userId) {
        log.debug("댓글 좋아요 취소 시작: commentId={}, userId={}", commentId, userId);
        CommentLike like = commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> LikeNotFoundException.forUserAndComment(userId, commentId));
        like.getComment().decreaseLikeCount();
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
        log.debug(
                "댓글 커서 리스트 조회 시작: articleId={}, orderBy={}, direction={}, limit={}, cursor={}, after={}, requesterId={}",
                articleId, orderBy, direction, limit, cursor, after, requesterId
        );
        // 1) 스펙 정의
        Specification<Comment> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            // articleId & 논리삭제 안 된 것
            predicates = cb.and(predicates,
                    cb.equal(root.get("article").get("id"), articleId),
                    cb.isNull(root.get("deletedAt"))
            );

            // 커서가 있으면 추가 조건
            if (cursor != null) {
                if (orderBy.equals("createdAt")) {
                    Instant curTime = Instant.parse(cursor);
                    if (direction.isDescending()) {
                        predicates = cb.and(predicates,
                                cb.lessThan(root.get("createdAt"), curTime));
                    } else {
                        predicates = cb.and(predicates,
                                cb.greaterThan(root.get("createdAt"), curTime));
                    }
                } else if (orderBy.equals("likeCount")) {
                    long curLike = Long.parseLong(cursor);
                    var likeExp = root.get("likeCount").as(Long.class);
                    if (direction.isDescending()) {
                        // likeCount < curLike OR (likeCount == curLike AND createdAt < after)
                        var p1 = cb.lessThan(likeExp, curLike);
                        var p2 = cb.and(
                                cb.equal(likeExp, curLike),
                                cb.lessThan(root.get("createdAt"), after)
                        );
                        predicates = cb.and(predicates, cb.or(p1, p2));
                    } else {
                        var p1 = cb.greaterThan(likeExp, curLike);
                        var p2 = cb.and(
                                cb.equal(likeExp, curLike),
                                cb.greaterThan(root.get("createdAt"), after)
                        );
                        predicates = cb.and(predicates, cb.or(p1, p2));
                    }
                }
            }

            return predicates;
        };

        // 2) 정렬: 기본 정렬 기준 + 생성시간 tie-breaker
        Sort sort = Sort.by(direction, orderBy)
                .and(Sort.by(direction, "createdAt"));

        // 3) 페이징: 항상 page=0, pageSize=limit
        Pageable pageable = PageRequest.of(0, limit, sort);

        // 4) 조회
        Page<Comment> page = commentRepository.findAll(spec, pageable);

        // 5) DTO 변환
        List<CommentDto> dtos = page.getContent().stream()
                .map(cmt -> {
                    boolean likedByMe = false; // 좋아요 여부 체크 로직 삽입
                    return commentMapper.toDto(cmt, likedByMe);
                })
                .collect(Collectors.toList());

        // 6) 다음 커서 계산
        String nextCursor = null;
        Instant nextAfter = null;
        if (page.hasNext() && !dtos.isEmpty()) {
            Comment last = page.getContent().get(page.getContent().size() - 1);
            Object primary = orderBy.equals("createdAt")
                    ? last.getCreatedAt()
                    : last.getLikeCount();
            nextCursor = primary.toString();
            nextAfter = last.getCreatedAt();
        }

        CursorPageResponse<CommentDto> response = new CursorPageResponse<>(
                dtos,
                nextCursor,
                nextAfter,
                dtos.size(),
                page.getTotalElements(),
                page.hasNext()
        );

        log.info(
                "댓글 커서 리스트 조회 완료: size={}, totalElements={}, hasNext={}",
                response.size(), response.totalElements(), response.hasNext()
        );
        return response;
    }
}
