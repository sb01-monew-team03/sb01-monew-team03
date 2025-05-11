package team03.monew.service.article;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import team03.monew.dto.article.ArticleDto;
import team03.monew.dto.article.ArticleFindRequest;
import team03.monew.dto.article.ArticleRestoreDto;
import team03.monew.dto.article.ArticleViewDto;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.entity.article.Article;

public interface ArticleService {

    // 뉴스 기사 목록 조회
    CursorPageResponse<ArticleDto> findArticles(ArticleFindRequest request, UUID userId);

    // 출처 목록 조회
    List<String> getSources();

    // 뉴스 복구
    List<ArticleRestoreDto> restore(Instant from, Instant to);

    // 기사 뷰 등록
    ArticleViewDto registerView(UUID articleId, UUID userId);

    // 논리 삭제
    void softDelete(UUID articleId);

    // 물리 삭제
    void hardDelete(UUID articleId);

    Article addInterestToArticle(UUID articleId, UUID interestId);

    List<Article> saveArticlesWithInterests(List<Article> articles, List<UUID> interestIds);
}
