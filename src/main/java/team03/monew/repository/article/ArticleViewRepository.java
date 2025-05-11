package team03.monew.repository.article;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team03.monew.entity.article.Article;
import team03.monew.entity.article.ArticleView;
import team03.monew.entity.user.User;

@Repository
public interface ArticleViewRepository extends JpaRepository<ArticleView, UUID> {

    boolean existsByArticleIdAndUserId(UUID articleId, UUID userId);

    Optional<ArticleView> findByArticleIdAndUserId(UUID articleId, UUID userId);

    long countByArticle(Article article);

    List<ArticleView> findTop10ByUserOrderByViewedAtDesc(User user);

}
