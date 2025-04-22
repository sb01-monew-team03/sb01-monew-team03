package team03.monew.service.comments;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.dto.comments.CommentDto;
import team03.monew.dto.comments.CommentUpdateRequest;
import team03.monew.entity.comments.Comment;
import team03.monew.mapper.comments.CommentMapper;
import team03.monew.repository.comments.CommentRepository;
import team03.monew.util.exception.comments.CommentNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    /*TODO Article 병합후 작성
    // 1. 댓글 등록
    public CommentDto create(CommentCreateRequest request) {
        log.debug("댓글 등록 시작: articleId={}, userId={}", request.articleId(), request.userId());

        Comment comment = new Comment(article, user, request.content());

        Comment createdComment = commentRepository.save(comment);
        log.info("댓글 등록 완료: commentId={}", createdComment.getId());

        return commentMapper.toDto(createdComment);
    }
    */


    // 2. 댓글 조회 (특정 게시글의 모든 댓글 조회)
    @Transactional(readOnly = true)
    public List<CommentDto> findByArticleId(UUID articleId) {
        log.debug("댓글 조회 시작: articleId={}", articleId);
        List<Comment> comments = commentRepository.findByArticleId(articleId);

        if (comments.isEmpty()) {
            throw CommentNotFoundException.withArticleId(articleId);
        }

        log.info("댓글 조회 완료: 댓글 수={}", comments.size());
        return comments.stream()
                .map(commentMapper::toDto)
                .toList();
    }

    // 3. 댓글 수정
    public CommentDto update(UUID commentId, UUID userId, CommentUpdateRequest request) {
        log.debug("댓글 수정 시작: commentId={}", commentId);

        // 댓글 존재 여부 확인
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> CommentNotFoundException.withId(commentId));

        // 댓글 수정
        comment.update(request.content());
        log.info("댓글 수정 완료: commentId={}", comment.getId());

        return commentMapper.toDto(comment);
    }

    // 4. 댓글 삭제 (논리적 삭제)
    public void softDelete(UUID commentId, UUID userId) {
        log.debug("댓글 삭제 시작: commentId={}", commentId);

        // 댓글 존재 여부 확인
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> CommentNotFoundException.withId(commentId));

        // 댓글 삭제 처리
        comment.delete();
        log.info("댓글 삭제 완료: commentId={}", comment.getId());
    }

    // 5. 댓글 물리적 삭제
    public void hardDelete(UUID commentId, UUID userId) {
        log.debug("댓글 물리 삭제 시작: commentId={}", commentId);

        // 댓글 존재 여부 확인
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> CommentNotFoundException.withId(commentId));

        // 댓글 물리적 삭제
        commentRepository.delete(comment);
        log.info("댓글 물리 삭제 완료: commentId={}", commentId);
    }
}