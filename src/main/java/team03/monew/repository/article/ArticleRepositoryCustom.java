package team03.monew.repository.article;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import team03.monew.entity.article.Article;

public interface ArticleRepositoryCustom {

    List<Article> findAllByCursor(
        String keyword,
        UUID interestId,
        List<String> sourceIn,
        LocalDateTime publishDateFrom,
        LocalDateTime publishDateTo,
        String orderBy,
        String direction,
        String cursor,
        LocalDateTime after,
        int limit
    );

    long countAllByCondition(
        String keyword,
        UUID interestId,
        List<String> sourceIn,
        LocalDateTime publishDateFrom,
        LocalDateTime publishDateTo
    );
}
