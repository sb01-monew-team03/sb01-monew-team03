package team03.monew.controller.comments;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team03.monew.dto.comments.*;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.service.comments.CommentService;
import team03.monew.util.exception.comments.AlreadyLikedException;
import team03.monew.util.exception.comments.LikeNotFoundException;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private static final int DEFAULT_LIMIT = 10;

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<?> listByArticle(
            @ModelAttribute CommentListRequest req,
            @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        log.info("댓글 목록 조회 요청: articleId={}, orderBy={}, direction={}, limit={}, cursor={}, after={}, requesterId={}",
                req.getArticleId(), req.getOrderBy(), req.getDirection(), req.getLimit(),
                req.getCursor(), req.getAfter(), requesterId
        );

        if (req.getLimit() != DEFAULT_LIMIT) {
            CursorPageResponse<CommentDto> page =
                    commentService.listByArticleCursor(
                            req.getArticleId(),
                            req.getOrderBy(),
                            req.getDirection(),
                            req.getLimit(),
                            req.getCursor(),
                            req.getAfter(),
                            requesterId
                    );
            log.info("댓글 커서 조회 완료: size={}, totalElements={}, hasNext={}",
                    page.size(), page.totalElements(), page.hasNext()
            );
            return ResponseEntity.ok(page);
        }

        // limit=10 이면 Page 기반으로
        Page<CommentDto> page = commentService.listByArticle(
                req.getArticleId(),
                req.getOrderBy(),
                req.getDirection(),
                req.getLimit(),
                requesterId
        );
        log.info("댓글 페이징 조회 완료: size={}, totalElements={}, hasNext={}",
                page.getSize(), page.getTotalElements(), page.hasNext()
        );
        return ResponseEntity.ok(page);
    }

    @PostMapping
    public ResponseEntity<CommentDto> create(@RequestBody @Valid CommentCreateRequest request) {
        log.info("댓글 등록 요청: {}", request);
        CommentDto dto = commentService.create(request);
        log.info("댓글 등록 완료: commentId={}", dto.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(
            @PathVariable UUID commentId,
            @RequestHeader("MoNew-Request-User-ID") UUID requesterId,
            @RequestBody @Valid CommentUpdateRequest request
    ) {
        log.info("댓글 수정 요청: commentId={}, request={}", commentId, request);
        CommentDto dto = commentService.update(commentId, requesterId, request);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> softDelete(
            @PathVariable UUID commentId,
            @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        log.info("댓글 논리 삭제 요청: commentId={}", commentId);
        commentService.softDelete(commentId, requesterId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{commentId}/hard")
    public ResponseEntity<Void> hardDelete(
            @PathVariable UUID commentId,
            @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        log.info("댓글 물리 삭제 요청: commentId={}", commentId);
        commentService.hardDelete(commentId, requesterId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/comment-likes")
    public ResponseEntity<CommentLikeDto> likeComment(
            @PathVariable UUID commentId,
            @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        log.info("댓글 좋아요 요청: commentId={}", commentId);
        CommentLikeDto dto = commentService.likeComment(commentId, requesterId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{commentId}/comment-likes")
    public ResponseEntity<Void> unlikeComment(
            @PathVariable UUID commentId,
            @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        log.info("댓글 좋아요 취소 요청: commentId={}", commentId);
        commentService.unlikeComment(commentId, requesterId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(AlreadyLikedException.class)
    public ResponseEntity<Void> handleAlreadyLiked(AlreadyLikedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(LikeNotFoundException.class)
    public ResponseEntity<Void> handleLikeNotFound(LikeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
