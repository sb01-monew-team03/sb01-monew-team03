package team03.monew.service.comments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import team03.monew.dto.comments.CommentCreateRequest;
import team03.monew.dto.comments.CommentDto;
import team03.monew.dto.comments.CommentLikeDto;
import team03.monew.dto.comments.CommentUpdateRequest;
import team03.monew.dto.common.CursorPageResponse;

import java.time.Instant;
import java.util.UUID;

public interface CommentService {

    CommentDto create(CommentCreateRequest request);

    Page<CommentDto> listByArticle(UUID articleId,
                                   String orderBy,
                                   Sort.Direction direction,
                                   int limit,
                                   UUID requesterId);


    CommentDto update(UUID commentId, UUID userId, CommentUpdateRequest request);


    void softDelete(UUID commentId, UUID userId);


    void hardDelete(UUID commentId, UUID userId);


    CommentLikeDto likeComment(UUID commentId, UUID userId);


    void unlikeComment(UUID commentId, UUID userId);

    CursorPageResponse<CommentDto> listByArticleCursor(
            UUID articleId,
            String orderBy,
            Sort.Direction direction,
            int limit,
            String cursor,
            Instant after,
            UUID requesterId
    );
}