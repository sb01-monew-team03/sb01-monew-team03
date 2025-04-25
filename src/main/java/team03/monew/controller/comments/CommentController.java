package team03.monew.controller.comments;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team03.monew.dto.comments.CommentCreateRequest;
import team03.monew.dto.comments.CommentDto;
import team03.monew.dto.comments.CommentLikeDto;
import team03.monew.dto.comments.CommentUpdateRequest;
import team03.monew.service.comments.CommentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;


    @GetMapping
    public ResponseEntity<Page<CommentDto>> listByArticle(
            @RequestParam UUID articleId,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        log.info("댓글 목록 조회 요청: articleId={}, orderBy={}, direction={}, limit={}, requesterId={}",
                articleId, orderBy, direction, limit, requesterId);
        Page<CommentDto> page = commentService.listByArticle(articleId, orderBy, direction, limit, requesterId);
        log.debug("댓글 목록 조회 응답: size={}, totalElements={}", page.getSize(), page.getTotalElements());
        return ResponseEntity.ok(page);
    }


    @PostMapping
    public ResponseEntity<CommentDto> create(
            @RequestBody @Valid CommentCreateRequest request
    ) {
        log.info("댓글 등록 요청: {}", request);
        CommentDto dto = commentService.create(request);
        log.debug("댓글 등록 응답: {}", dto);
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
        log.debug("댓글 수정 응답: {}", dto);
        return ResponseEntity.ok(dto);
    }


    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> softDelete(
            @PathVariable UUID commentId,
            @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        log.info("댓글 논리 삭제 요청: commentId={}, requesterId={}", commentId, requesterId);
        commentService.softDelete(commentId, requesterId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{commentId}/hard")
    public ResponseEntity<Void> hardDelete(
            @PathVariable UUID commentId,
            @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        log.info("댓글 물리 삭제 요청: commentId={}, requesterId={}", commentId, requesterId);
        commentService.hardDelete(commentId, requesterId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{commentId}/comment-likes")
    public ResponseEntity<CommentLikeDto> like(
            @PathVariable UUID commentId,
            @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        log.info("댓글 좋아요 요청: commentId={}, requesterId={}", commentId, requesterId);
        CommentLikeDto dto = commentService.likeComment(commentId, requesterId);
        log.debug("댓글 좋아요 응답: {}", dto);
        return ResponseEntity.ok(dto);
    }


    @DeleteMapping("/{commentId}/comment-likes")
    public ResponseEntity<Void> unlike(
            @PathVariable UUID commentId,
            @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        log.info("댓글 좋아요 취소 요청: commentId={}, requesterId={}", commentId, requesterId);
        commentService.unlikeComment(commentId, requesterId);
        return ResponseEntity.ok().build();
    }
}