package team03.monew.controller.article;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team03.monew.dto.article.ArticleDto;
import team03.monew.dto.article.ArticleRestoreDto;
import team03.monew.dto.article.ArticleViewDto;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.service.article.ArticleService;

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ArticleService articleService;

    @Nested
    @DisplayName("createView() - 기사 뷰 등록")
    class CreateViewTest {

        @Test
        @DisplayName("기사 뷰 등록 성공")
        void createView_success() throws Exception {
            UUID articleId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            ArticleViewDto dto = new ArticleViewDto(
                UUID.randomUUID(), userId, Instant.now(), articleId,
                "NAVER", "naver.com", "title", Instant.now(), "summary", 1L, 10L
            );

            given(articleService.registerView(articleId, userId)).willReturn(dto);

            mockMvc.perform(post("/api/articles/{articleId}/article-views", articleId)
                    .header("Monew-Request-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(articleId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
        }
    }

    @Nested
    @DisplayName("find() - 뉴스 기사 목록 조회")
    class FindArticlesTest {

        @Test
        @DisplayName("뉴스 기사 목록 조회 성공")
        void find_success() throws Exception {
            UUID userId = UUID.randomUUID();
            UUID articleId = UUID.randomUUID();

            ArticleDto dto = mock(ArticleDto.class);
            CursorPageResponse<ArticleDto> response = new CursorPageResponse<>(
                List.of(dto), null, null, 1, 10L, false);

            given(articleService.findArticles(any(), eq(userId))).willReturn(response);

            mockMvc.perform(get("/api/articles")
                    .header("Monew-Request-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
        }
    }

    @Nested
    @DisplayName("findSources() - 출처 목록 조회")
    class FindSourcesTest {

        @Test
        @DisplayName("출처 목록 조회 성공")
        void findSources_success() throws Exception {
            List<String> sources = List.of("NAVER", "DAUM");
            given(articleService.getSources()).willReturn(sources);

            mockMvc.perform(get("/api/articles/sources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("NAVER"))
                .andExpect(jsonPath("$[1]").value("DAUM"));
        }
    }

    @Nested
    @DisplayName("restore() - 뉴스 복구")
    class RestoreTest {

        @Test
        @DisplayName("뉴스 복구 성공")
        void restore_success() throws Exception {
            Instant from = Instant.parse("2024-01-01T00:00:00Z");
            Instant to = Instant.parse("2024-01-03T00:00:00Z");

            ArticleRestoreDto dto = new ArticleRestoreDto(from, List.of(UUID.randomUUID()), 1L);
            given(articleService.restore(from, to)).willReturn(List.of(dto));

            mockMvc.perform(get("/api/articles/restore")
                    .param("from", from.toString())
                    .param("to", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].restoredCount").value(1L));
        }
    }

    @Nested
    @DisplayName("softDelete() - 기사 논리 삭제")
    class SoftDeleteTest {

        @Test
        @DisplayName("기사 논리 삭제 성공")
        void softDelete_success() throws Exception {
            UUID articleId = UUID.randomUUID();
            willDoNothing().given(articleService).softDelete(articleId);

            mockMvc.perform(delete("/api/articles/{articleId}", articleId))
                .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("hardDelete() - 기사 물리 삭제")
    class HardDeleteTest {

        @Test
        @DisplayName("기사 물리 삭제 성공")
        void hardDelete_success() throws Exception {
            UUID articleId = UUID.randomUUID();
            willDoNothing().given(articleService).hardDelete(articleId);

            mockMvc.perform(delete("/api/articles/{articleId}/hard", articleId))
                .andExpect(status().isNoContent());
        }
    }
}
