package team03.monew.repository.article;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import team03.monew.entity.article.Article;

public interface ArticleRepository extends JpaRepository<Article, UUID> {

}
