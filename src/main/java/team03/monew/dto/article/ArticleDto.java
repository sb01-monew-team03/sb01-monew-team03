package team03.monew.dto.article;

import java.time.LocalDateTime;
import java.util.UUID;

public record ArticleDto(
    UUID id,
    String source,
    String sourceUrl,
    String title,
    LocalDateTime publishDate,
    String summary,
    int commentCount,
    int viewCount,
    boolean viewedByMe
) {

}