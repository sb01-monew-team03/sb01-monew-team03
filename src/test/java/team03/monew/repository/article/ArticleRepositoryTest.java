package team03.monew.repository.article;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import team03.monew.config.JpaConfig;
import team03.monew.config.QueryDslConfig;
import team03.monew.entity.article.Article;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Keyword;
import team03.monew.entity.user.User;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaConfig.class})
@EntityScan(basePackageClasses = {Article.class, User.class, Interest.class, Keyword.class,
    Comment.class})
@EnableJpaRepositories(basePackageClasses = ArticleRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private TestEntityManager em;

    private Article article;

    @BeforeEach
    void setUp() {
        article = new Article(
            "NAVER",
            "https://naver.com/test",
            "Test Title",
            "Test summary",
            LocalDateTime.now()
        );
        articleRepository.save(article);
    }

    @Test
    @DisplayName("originalLink로 기사 존재 여부 확인")
    void existsByOriginalLink() {
        boolean exists = articleRepository.existsByOriginalLink(article.getOriginalLink());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("originalLink로 삭제되지 않은 기사 조회")
    void findByOriginalLinkAndDeletedAtIsNull() {
        Optional<Article> found = articleRepository.findByOriginalLinkAndDeletedAtIsNull(
            article.getOriginalLink());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo(article.getTitle());
    }

    @Test
    @DisplayName("id로 삭제되지 않은 기사 조회")
    void findByIdAndDeletedAtIsNull() {
        Optional<Article> found = articleRepository.findByIdAndDeletedAtIsNull(article.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo(article.getTitle());
    }

    @Test
    @DisplayName("발행일 범위로 기사 리스트 조회")
    void findAllByPublishedAtBetween() {
        LocalDateTime now = LocalDateTime.now();
        List<Article> results = articleRepository.findAllByPublishedAtBetween(now.minusDays(1),
            now.plusDays(1));
        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("조건 기반 기사 페이지 조회")
    void findArticlesByConditions() {
        Page<Article> result = articleRepository.findArticlesByConditions(
            "Test", List.of("NAVER"), LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1),
            PageRequest.of(0, 10)
        );
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getSource()).isEqualTo("NAVER");
    }

    @Test
    @DisplayName("Custom: countAllByCondition() 테스트")
    void countAllByCondition() {
        long count = articleRepository.countAllByCondition(
            "Test", null, List.of("NAVER"), LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1)
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Custom: findAllByCursor() 기본 테스트")
    void findAllByCursor() {
        List<Article> result = articleRepository.findAllByCursor(
            "Test", null, List.of("NAVER"),
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1),
            "publishDate", "desc",
            null, null, 10
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(article.getTitle());
    }
}
