package team03.monew.dto.comments;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * 댓글 생성 요청 DTO
 */
public record CommentCreateRequest(
        @NotNull(message = "기사 ID는 필수입니다.")
        UUID articleId,

        @NotNull(message = "사용자 ID는 필수입니다.")
        UUID userId,

        @NotBlank(message = "댓글 내용은 필수입니다.")
        String content
) {}