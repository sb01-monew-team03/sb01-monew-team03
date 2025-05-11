package team03.monew.service.article;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
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
import team03.monew.entity.user.User;
import team03.monew.mapper.article.ArticleMapper;
import team03.monew.repository.article.ArticleRepository;
import team03.monew.repository.article.ArticleViewRepository;
import team03.monew.repository.comments.CommentRepository;
import team03.monew.repository.user.UserRepository;
import team03.monew.util.exception.article.ArticleNotFoundException;
import team03.monew.util.exception.user.UserNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleViewRepository articleViewRepository;
    private final ArticleMapper articleMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<ArticleDto> findArticles(ArticleFindRequest request, UUID userId) {
        log.debug("기사 목록 조회 시작");

        LocalDateTime after = request.after() != null
            ? LocalDateTime.ofInstant(request.after(), ZoneId.systemDefault())
            : null;

        List<Article> articles = articleRepository.findAllByCursor(
            request.keyword(),
            request.interestId(),
            request.sourceIn(),
            request.publishDateFrom(),
            request.publishDateTo(),
            request.orderBy(),
            request.direction(),
            request.cursor(),
            after,
            request.limit() + 1
        );

        boolean hasNext = articles.size() > request.limit();
        if (hasNext) {
            articles.remove(articles.size() - 1);
        }

        List<ArticleDto> articleDtos = articles.stream().map(article -> {
            long commentCount = commentRepository.countByArticle(article);
            boolean viewedByMe = articleViewRepository.existsByArticleIdAndUserId(article.getId(),
                userId);
            return articleMapper.toDto(article, (int) commentCount, viewedByMe);
        }).toList();

        String nextCursor = null;
        Instant nextAfter = null;

        if (hasNext && !articles.isEmpty()) {
            Article last = articles.get(articles.size() - 1);
            nextAfter = last.getPublishedAt().atZone(ZoneId.systemDefault()).toInstant();

            switch (request.orderBy()) {
                case "commentCount" ->
                    nextCursor = String.valueOf(commentRepository.countByArticle(last));
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

        return new CursorPageResponse<>(
            articleDtos,
            nextCursor,
            nextAfter,
            articleDtos.size(),
            totalElements,
            hasNext
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
    @Transactional
    public ArticleViewDto registerView(UUID articleId, UUID userId) {
        Article article = articleRepository.findByIdAndDeletedAtIsNull(articleId)
            .orElseThrow(() -> ArticleNotFoundException.withId(articleId));

        User user = userRepository.findActiveById(userId)
            .orElseThrow(() -> UserNotFoundException.withId(userId));

        // 이미 본 적 있다면 리턴
        Optional<ArticleView> existing = articleViewRepository.findByArticleIdAndUserId(articleId,
            userId);
        if (existing.isPresent()) {
            long commentCount = commentRepository.countByArticle(article);
            return articleMapper.toViewDto(existing.get(), article, commentCount);
        }

        // 새로 조회 등록
        ArticleView view = new ArticleView(user, article);
        articleViewRepository.save(view);

        article.increaseViewCount();

        long commentCount = commentRepository.countByArticle(article);
        return articleMapper.toViewDto(view, article, commentCount);
    }

    @Override
    public List<String> getSources() {
        return List.of("HANKYUNG", "CHOSUN");
    }
}
