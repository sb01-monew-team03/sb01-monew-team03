package team03.monew.dto.article;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record ArticleDto(
        UUID id,
        String source,
        String originalLink,
        String title,
        String summary,
        LocalDateTime publishedAt,
        int viewCount,
        Set<String> interestNames
) {}