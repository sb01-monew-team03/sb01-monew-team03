package team03.monew.service.article;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team03.monew.dto.article.ArticleDto;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.dto.article.ArticleFindRequest;
import team03.monew.dto.article.ArticleRestoreDto;
import team03.monew.dto.article.ArticleViewDto;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.entity.article.Article;
import team03.monew.entity.article.ArticleView;
import team03.monew.mapper.article.ArticleMapper;
import team03.monew.repository.article.ArticleRepository;
import team03.monew.repository.article.ArticleViewRepository;
import team03.monew.util.exception.article.ArticleNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleViewRepository articleViewRepository;
    private final ArticleMapper articleMapper;

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<ArticleDto> findArticles(ArticleFindRequest request, UUID userId) {
        log.debug("기사 목록 조회 시작: request={}, userId={}", request, userId);

        List<Article> articles = articleRepository.findAllByCursor(
            request.keyword(),
            request.interestId(),
            request.sourceIn(),
            request.publishDateFrom(),
            request.publishDateTo(),
            request.orderBy(),
            request.direction(),
            request.cursor(),
            request.after(),
            request.limit()
        );

        Set<UUID> viewedArticleIds = new HashSet<>(
            articleViewRepository.findViewedArticleIds(userId));

        List<ArticleDto> articleDtos = articles.stream()
            .limit(request.limit())
            .map(article -> {
                boolean viewedByMe = viewedArticleIds.contains(article.getId());
                return articleMapper.toDto(article, viewedByMe);
            })
            .toList();

        String nextCursor = null;
        Instant nextAfter = null;

        if (articles.size() > request.limit()) {
            Article last = articles.get(request.limit());
            nextAfter = last.getPublishedAt().toInstant(ZoneOffset.UTC);

            switch (request.orderBy()) {
                case "commentCount" -> nextCursor = String.valueOf(last.getComments().size());
                case "viewCount" -> nextCursor = String.valueOf(last.getViewCount());
                default -> nextCursor = last.getPublishedAt().toString();
            }
        }

        long totalElements = articleRepository.countAllByCondition(
            request.keyword(),
            request.interestId(),
            request.sourceIn(),
            request.publishDateFrom(),
            request.publishDateTo()
        );

        log.info("기사 목록 조회 완료: size={}, total={}", articleDtos.size(), totalElements);

        return new CursorPageResponse<>(
            articleDtos,
            nextCursor,
            nextAfter,
            articleDtos.size(),
            totalElements,
            articles.size() > request.limit()
        );
    }

    @Override
    public List<ArticleRestoreDto> restore(Instant from, Instant to) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void softDelete(UUID articleId) {
        log.debug("기사 논리 삭제 시작: articleId={}", articleId);
        Article article = articleRepository.findByIdAndDeletedAtIsNull(articleId)
            .orElseThrow(() -> ArticleNotFoundException.withId(articleId));
        article.delete();
        log.info("기사 논리 삭제 완료: articleId={}", articleId);
    }

    @Override
    public void hardDelete(UUID articleId) {
        log.debug("기사 물리 삭제 시작: articleId={}", articleId);
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> ArticleNotFoundException.withId(articleId));
        articleRepository.delete(article);
        log.info("기사 물리 삭제 완료: articleId={}", articleId);
    }

    @Override
    public ArticleViewDto registerView(UUID articleId, UUID userId) {
        log.debug("기사 뷰 등록 시작: articleId={}, userId={}", articleId, userId);

        Article article = articleRepository.findByIdAndDeletedAtIsNull(articleId)
            .orElseThrow(() -> ArticleNotFoundException.withId(articleId));

        Optional<ArticleView> optionalView = articleViewRepository.findByArticleIdAndViewedBy(
            articleId, userId);

        if (optionalView.isEmpty()) {
            ArticleView view = new ArticleView(article, userId);
            article.increaseViewCount();
            articleViewRepository.save(view);
            log.info("기사 뷰 등록 완료: articleId={}, userId={}", articleId, userId);
            return articleMapper.toViewDto(view, article);
        }

        log.debug("이미 조회한 기사입니다: articleId={}, userId={}", articleId, userId);
        return articleMapper.toViewDto(optionalView.get(), article);
    }

    @Override
    public List<String> findSources() {
        return articleRepository.findDistinctSources();
    }
}
