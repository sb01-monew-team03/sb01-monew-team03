package team03.monew.controller.article;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team03.monew.config.api.ArticleApi;
import team03.monew.dto.article.ArticleDto;
import team03.monew.dto.article.ArticleFindRequest;
import team03.monew.dto.article.ArticleRestoreDto;
import team03.monew.dto.article.ArticleViewDto;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.service.article.ArticleService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController implements ArticleApi {

    private final ArticleService articleService;

    // 기사 뷰 등록
    @Override
    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<ArticleViewDto> createView(
        @PathVariable UUID articleId,
        @RequestHeader("Monew-Request-User-Id") UUID userId
    ) {
        log.info("기사 뷰 등록 요청: articleId={}, userId={}", articleId, userId);
        ArticleViewDto viewDto = articleService.registerView(articleId, userId);
        log.debug("기사 뷰 등록 응답: {}", viewDto);
        return ResponseEntity.ok(viewDto);
    }

    // 뉴스 기사 목록 조회
    @Override
    @GetMapping
    public ResponseEntity<CursorPageResponse<ArticleDto>> find(
        @ModelAttribute @Valid ArticleFindRequest request,
        @RequestHeader("Monew-Request-User-Id") UUID userId
    ) {
        log.info("기사 목록 조회 요청: userId={}, request={}", userId, request);
        CursorPageResponse<ArticleDto> result = articleService.findArticles(request, userId);
        log.debug("기사 목록 조회 응답: contentSize={}, nextCursor={}", result.content().size(),
            result.nextCursor());
        return ResponseEntity.ok(result);
    }

    // 출처 목록 조회
    @Override
    @GetMapping("/sources")
    public ResponseEntity<List<String>> findSources() {
        log.info("출처 목록 조회 요청");
        List<String> sources = articleService.getSources();
        log.debug("출처 목록 조회 응답: {}", sources);
        return ResponseEntity.ok(sources);
    }

    // 뉴스 복구
    @Override
    @GetMapping("/restore")
    public ResponseEntity<List<ArticleRestoreDto>> restore(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        log.info("뉴스 복구 요청: from={}, to={}", from, to);
        List<ArticleRestoreDto> results = articleService.restore(from, to);
        log.debug("뉴스 복구 응답: 복구된 건수={}", results.size());
        return ResponseEntity.ok(results);
    }

    // 기사 논리 삭제
    @Override
    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> softDelete(@PathVariable UUID articleId) {
        log.info("기사 논리 삭제 요청: articleId={}", articleId);
        articleService.softDelete(articleId);
        return ResponseEntity.noContent().build();
    }

    // 기사 물리 삭제
    @Override
    @DeleteMapping("/{articleId}/hard")
    public ResponseEntity<Void> hardDelete(@PathVariable UUID articleId) {
        log.info("기사 물리 삭제 요청: articleId={}", articleId);
        articleService.hardDelete(articleId);
        return ResponseEntity.noContent().build();
    }

}
