package team03.monew.service.article;

import jakarta.transaction.Transactional;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team03.monew.dto.article.ArticleRestoreDto;
import team03.monew.entity.article.Article;
import team03.monew.repository.article.ArticleRepository;
import team03.monew.service.csv.CsvService;
import team03.monew.service.s3.S3Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleRestoreServiceImpl implements ArticleRestoreService {

    private final ArticleRepository articleRepository;
    private final CsvService csvService;
    private final S3Service s3Service;

    @Transactional
    public ArticleRestoreDto restore(LocalDate from, LocalDate to) {
        List<Article> allRestoredArticles = new ArrayList<>();

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            Path csvPath = s3Service.download(date);
            List<Article> articlesFromCsv = csvService.importArticlesFromCsv(csvPath);

            log.info("{} 날짜의 CSV에서 {}개의 기사 로드됨", date, articlesFromCsv.size());
            allRestoredArticles.addAll(articlesFromCsv);
        }

        Map<String, Article> existingMap = articleRepository.findAll().stream()
            .collect(Collectors.toMap(Article::getOriginalLink, a -> a));

        List<Article> toRestore = new ArrayList<>();
        for (Article article : allRestoredArticles) {
            if (!existingMap.containsKey(article.getOriginalLink())) {
                toRestore.add(article);
            }
        }

        List<Article> saved = articleRepository.saveAll(toRestore);
        log.info("총 {}개의 기사 복구됨", saved.size());

        return new ArticleRestoreDto(
            LocalDateTime.now().toInstant(ZoneOffset.UTC),
            saved.stream().map(Article::getId).toList(),
            (long) saved.size()
        );
    }
}
