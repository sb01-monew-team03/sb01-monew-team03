package team03.monew.service.article;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team03.monew.dto.article.ArticleCreateRequest;
import team03.monew.dto.article.ArticleDto;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.entity.article.Article;
import team03.monew.mapper.article.ArticleMapper;
import team03.monew.repository.article.ArticleRepository;
import team03.monew.util.exception.article.ArticleNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    @Override
    public ArticleDto create(ArticleCreateRequest request) {
        log.debug("기사 등록 시작: {}", request);

        Article article = new Article(
            request.source(),
            request.originalLink(),
            request.title(),
            request.summary(),
            request.publishedAt()
        );

        Article saved = articleRepository.save(article);
        log.info("기사 등록 완료: articleId={}", saved.getId());
        return articleMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDto findById(UUID articleId) {
        log.debug("기사 단건 조회 시작: articleId={}", articleId);
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> ArticleNotFoundException.withId(articleId));
        log.info("기사 단건 조회 완료: title={}", article.getTitle());
        return articleMapper.toDto(article);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<ArticleDto> search() {
//        log.debug("기사 검색 시작: {}", condition);
//        List<Article> result = articleRepository.findAll(); // placeholder
//        return result.stream().map(articleMapper::toDto).toList();
//    }

    @Override
    public void softDelete(UUID articleId) {
        log.debug("기사 논리 삭제 시작: articleId={}", articleId);
        Article article = articleRepository.findById(articleId)
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
}