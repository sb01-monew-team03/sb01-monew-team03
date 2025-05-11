package team03.monew.dto.article;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ArticleRestoreDto(
    Instant restoreDate,
    List<UUID> restoredArticleIds,
    Long restoredArticleCount
) {

}
