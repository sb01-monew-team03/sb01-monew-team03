package team03.monew.dto.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ArticleCreateRequest(
        @NotBlank String source,
        @NotBlank String originalLink,
        @NotBlank String title,
        @NotBlank String summary,
        @NotNull LocalDateTime publishedAt
) {}