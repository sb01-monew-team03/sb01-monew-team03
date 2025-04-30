package team03.monew.controller.comments;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import team03.monew.dto.comments.CommentCreateRequest;
import team03.monew.dto.comments.CommentDto;
import team03.monew.dto.comments.CommentLikeDto;
import team03.monew.dto.comments.CommentUpdateRequest;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.service.comments.CommentService;
import team03.monew.util.exception.comments.CommentNotFoundException;
import team03.monew.util.exception.comments.AlreadyLikedException;
import team03.monew.util.exception.comments.LikeNotFoundException;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @Nested
    @DisplayName("댓글 목록 조회 (Page 기반)")
    class ListCommentsTest {
        @Test @DisplayName("정상 조회")
        void listByArticle_success() throws Exception {
            UUID articleId = UUID.randomUUID();
            UUID reqId = UUID.randomUUID();

            CommentDto dto = new CommentDto(
                    UUID.randomUUID(), articleId, UUID.randomUUID(),
                    "nick","hello",3L,true, Instant.now()
            );
            // 서비스는 Page<CommentDto> 반환
            given(commentService.listByArticle(
                    articleId, "createdAt", Sort.Direction.DESC, 10, reqId
            )).willReturn(new PageImpl<>(List.of(dto)));

            mockMvc.perform(get("/api/comments")
                            .param("articleId", articleId.toString())
                            .param("orderBy", "createdAt")
                            .param("direction", "DESC")
                            .param("limit", "10")
                            .header("MoNew-Request-User-ID", reqId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(dto.id().toString()));
        }
    }

    @Nested
    @DisplayName("댓글 페이지네이션 조회 (Cursor 기반)")
    class CursorPaginationTest {
        @Test @DisplayName("커서 페이지네이션 정상 조회")
        void cursorPagination_success() throws Exception {
            UUID articleId = UUID.randomUUID();
            UUID reqId = UUID.randomUUID();
            Instant createdAt = Instant.parse("2025-04-20T10:15:30Z");

            CommentDto dto = new CommentDto(
                    UUID.randomUUID(), articleId, UUID.randomUUID(),
                    "nick","cursor!",2L,false, createdAt
            );
            CursorPageResponse<CommentDto> page = new CursorPageResponse<>(
                    List.of(dto),
                    null,
                    null,
                    1,
                    1L,
                    false
            );

            given(commentService.listByArticleCursor(
                    articleId, "createdAt", Sort.Direction.DESC,
                    5, null, null, reqId
            )).willReturn(page);

            mockMvc.perform(get("/api/comments")
                            .param("articleId", articleId.toString())
                            .param("orderBy", "createdAt")
                            .param("direction", "DESC")
                            .param("limit", "5")
                            .header("MoNew-Request-User-ID", reqId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(dto.id().toString()))
                    .andExpect(jsonPath("$.size").value(1))
                    .andExpect(jsonPath("$.hasNext").value(false));
        }
    }


    @Nested
    @DisplayName("댓글 등록")
    class CreateCommentTest {
        @Test
        @DisplayName("댓글 등록 성공")
        void create_success() throws Exception {
            String content = "content";
            UUID articleId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            CommentCreateRequest request = new CommentCreateRequest(content, articleId, userId);
            CommentDto dto = new CommentDto(
                    UUID.randomUUID(), articleId, userId,
                    "nick", content, 0L, false,
                    Instant.now()
            );
            given(commentService.create(request)).willReturn(dto);

            mockMvc.perform(post("/api/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(dto.id().toString()))
                    .andExpect(jsonPath("$.content").value(content));
        }

        @Test
        @DisplayName("잘못된 요청 - 등록 실패")
        void create_badRequest() throws Exception {
            String emptyContent = "";
            UUID articleId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            CommentCreateRequest invalid = new CommentCreateRequest(emptyContent, articleId, userId);

            mockMvc.perform(post("/api/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class UpdateCommentTest {
        @Test
        @DisplayName("댓글 수정 성공")
        void update_success() throws Exception {
            UUID commentId = UUID.randomUUID();
            UUID requesterId = UUID.randomUUID();
            CommentUpdateRequest request = new CommentUpdateRequest("updated");
            CommentDto dto = new CommentDto(
                    commentId, UUID.randomUUID(), requesterId,
                    "nick", request.content(), 1L, false,
                    Instant.now()
            );
            given(commentService.update(commentId, requesterId, request)).willReturn(dto);

            mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("MoNew-Request-User-ID", requesterId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").value(request.content()));
        }

        @Test
        @DisplayName("댓글 없음 - 수정 실패")
        void update_notFound() throws Exception {
            UUID commentId = UUID.randomUUID();
            UUID requesterId = UUID.randomUUID();
            CommentUpdateRequest request = new CommentUpdateRequest("x");
            given(commentService.update(commentId, requesterId, request))
                    .willThrow(CommentNotFoundException.withId(commentId));

            mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("MoNew-Request-User-ID", requesterId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class DeleteCommentTest {
        @Test
        @DisplayName("논리 삭제 성공")
        void softDelete_success() throws Exception {
            UUID commentId = UUID.randomUUID();
            UUID requesterId = UUID.randomUUID();
            willDoNothing().given(commentService).softDelete(commentId, requesterId);

            mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                            .header("MoNew-Request-User-ID", requesterId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("댓글 없음 - 논리 삭제 실패")
        void softDelete_notFound() throws Exception {
            UUID commentId = UUID.randomUUID();
            UUID requesterId = UUID.randomUUID();
            willThrow(CommentNotFoundException.withId(commentId))
                    .given(commentService).softDelete(commentId, requesterId);

            mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                            .header("MoNew-Request-User-ID", requesterId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("물리 삭제 성공")
        void hardDelete_success() throws Exception {
            UUID commentId = UUID.randomUUID();
            UUID requesterId = UUID.randomUUID();
            willDoNothing().given(commentService).hardDelete(commentId, requesterId);

            mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
                            .header("MoNew-Request-User-ID", requesterId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("댓글 없음 - 물리 삭제 실패")
        void hardDelete_notFound() throws Exception {
            UUID commentId = UUID.randomUUID();
            UUID requesterId = UUID.randomUUID();
            willThrow(CommentNotFoundException.withId(commentId))
                    .given(commentService).hardDelete(commentId, requesterId);

            mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
                            .header("MoNew-Request-User-ID", requesterId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("댓글 좋아요")
    class LikeCommentTest {
        @Test
        @DisplayName("좋아요 등록 성공")
        void like_success() throws Exception {
            UUID commentId = UUID.randomUUID();
            UUID requesterId = UUID.randomUUID();
            CommentLikeDto dto = new CommentLikeDto(
                    UUID.randomUUID(), requesterId, Instant.now(),
                    commentId, UUID.randomUUID(), UUID.randomUUID(), "nick",
                    "content", 0L, Instant.now()
            );
            given(commentService.likeComment(commentId, requesterId)).willReturn(dto);

            mockMvc.perform(post("/api/comments/{commentId}/comment-likes", commentId)
                            .header("MoNew-Request-User-ID", requesterId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.commentId").value(commentId.toString()));
        }

        @Test
        @DisplayName("이미 좋아요된 댓글 - 등록 실패")
        void like_already() throws Exception {
            UUID commentId = UUID.randomUUID();
            UUID requesterId = UUID.randomUUID();
            willThrow(AlreadyLikedException.class)
                    .given(commentService).likeComment(commentId, requesterId);

            mockMvc.perform(post("/api/comments/{commentId}/comment-likes", commentId)
                            .header("MoNew-Request-User-ID", requesterId))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("댓글 좋아요 취소")
    class UnlikeCommentTest {
        @Test
        @DisplayName("좋아요 취소 성공")
        void unlike_success() throws Exception {
            UUID commentId = UUID.randomUUID();
            UUID requesterId = UUID.randomUUID();
            willDoNothing().given(commentService).unlikeComment(commentId, requesterId);

            mockMvc.perform(delete("/api/comments/{commentId}/comment-likes", commentId)
                            .header("MoNew-Request-User-ID", requesterId))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("좋아요 정보 없음 - 취소 실패")
        void unlike_notFound() throws Exception {
            UUID commentId = UUID.randomUUID();
            UUID requesterId = UUID.randomUUID();
            willThrow(LikeNotFoundException.class)
                    .given(commentService).unlikeComment(commentId, requesterId);

            mockMvc.perform(delete("/api/comments/{commentId}/comment-likes", commentId)
                            .header("MoNew-Request-User-ID", requesterId))
                    .andExpect(status().isNotFound());
        }
    }
}
