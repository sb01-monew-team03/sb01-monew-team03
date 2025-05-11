package team03.monew.repository.article;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team03.monew.entity.article.Article;

public interface ArticleRepository extends JpaRepository<Article, UUID>, ArticleRepositoryCustom {

    Optional<Article> findByOriginalLinkAndDeletedAtIsNull(String originalLink);

    Optional<Article> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByOriginalLink(String originalLink);

    @Query("""
            SELECT a FROM Article a
            WHERE a.deletedAt IS NULL
              AND (:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(a.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:sourceIn IS NULL OR a.source IN :sourceIn)
              AND (:publishDateFrom IS NULL OR a.publishedAt >= :publishDateFrom)
              AND (:publishDateTo IS NULL OR a.publishedAt <= :publishDateTo)
        """)
    Page<Article> findArticlesByConditions(
        @Param("keyword") String keyword,
        @Param("sourceIn") List<String> sourceIn,
        @Param("publishDateFrom") LocalDateTime publishDateFrom,
        @Param("publishDateTo") LocalDateTime publishDateTo,
        Pageable pageable
    );
}
