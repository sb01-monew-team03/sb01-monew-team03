package team03.monew.service.article;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team03.monew.entity.article.Article;
import team03.monew.mapper.article.ArticleMapper;
import team03.monew.repository.article.ArticleRepository;
import team03.monew.util.exception.article.ArticleNotFoundException;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleMapper articleMapper;

    @Nested
    @DisplayName("softDelete() - 기사 논리 삭제")
    class SoftDeleteTest {

        @Test
        @DisplayName("기사 존재 - 삭제 성공")
        void softDelete_success() {
            // given
            UUID id = UUID.randomUUID();
            Article article = mock(Article.class);
            given(articleRepository.findById(id)).willReturn(Optional.of(article));

            // when
            articleService.softDelete(id);

            // then
            then(article).should().delete();
        }

        @Test
        @DisplayName("기사 없음 - 예외 발생")
        void softDelete_notFound() {
            // given
            UUID id = UUID.randomUUID();
            given(articleRepository.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThrows(ArticleNotFoundException.class, () -> articleService.softDelete(id));
        }
    }

    @Nested
    @DisplayName("hardDelete() - 기사 물리 삭제")
    class HardDeleteTest {

        @Test
        @DisplayName("기사 존재 - 삭제 성공")
        void hardDelete_success() {
            // given
            UUID id = UUID.randomUUID();
            Article article = mock(Article.class);
            given(articleRepository.findById(id)).willReturn(Optional.of(article));

            // when
            articleService.hardDelete(id);

            // then
            then(articleRepository).should().delete(article);
        }

        @Test
        @DisplayName("기사 없음 - 예외 발생")
        void hardDelete_notFound() {
            // given
            UUID id = UUID.randomUUID();
            given(articleRepository.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThrows(ArticleNotFoundException.class, () -> articleService.hardDelete(id));
        }
    }
}
