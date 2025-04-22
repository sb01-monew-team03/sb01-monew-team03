package team03.monew.dto.comments;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateRequest(
        @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다.")
        String content
) {}