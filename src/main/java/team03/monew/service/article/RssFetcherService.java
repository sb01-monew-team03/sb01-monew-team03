package team03.monew.service.article;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team03.monew.entity.article.Article;
import team03.monew.entity.article.ArticleInterest;
import team03.monew.entity.interest.Interest;
import team03.monew.repository.article.ArticleRepository;
import team03.monew.service.interest.InterestMatchingService;
import team03.monew.service.notification.NotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RssFetcherService {

    private final ArticleRepository articleRepository;
    private final InterestMatchingService interestMatchingService;
    private final NotificationService notificationService;

    public void fetchAll() {
        List<Article> allSavedArticles = new ArrayList<>();

        allSavedArticles.addAll(fetchFrom("https://www.hankyung.com/feed/all-news", "HANKYUNG"));
        allSavedArticles.addAll(fetchFrom("https://www.chosun.com/arc/outboundfeeds/rss/?outputType=xml", "CHOSUN"));
        //allSavedArticles.addAll(fetchFrom("http://www.yonhapnewstv.co.kr/browse/feed/", "YONHAP"));

        // 모든 기사가 저장된 후 알림 생성
        if (!allSavedArticles.isEmpty()) {
            try {
                notificationService.createInterestNotification(allSavedArticles);
                log.info("새 기사 {}개에 대한 알림 생성 완료", allSavedArticles.size());
            } catch (Exception e) {
                log.error("알림 생성 중 오류 발생: {}", e.getMessage(), e);
            }
        }
    }

    private List<Article> fetchFrom(String feedUrl, String source) {
        List<Article> savedArticles = new ArrayList<>();

        try (InputStream inputStream = new URL(feedUrl).openStream();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            SyndFeed feed = new SyndFeedInput().build(reader);
            List<SyndEntry> entries = feed.getEntries();

            List<Article> articlesToSave = new ArrayList<>();

            for (SyndEntry entry : entries) {
                String link = entry.getLink();
                if (articleRepository.existsByOriginalLink(link)) {
                    continue;
                }

                LocalDateTime publishedAt = entry.getPublishedDate() != null
                    ? entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    : LocalDateTime.now();

                String summary =
                    entry.getDescription() != null ? entry.getDescription().getValue() : "";

                Article article = new Article(
                    source,
                    link,
                    entry.getTitle(),
                    summary,
                    publishedAt
                );

                articlesToSave.add(article);
            }

            if (!articlesToSave.isEmpty()) {
                log.info("[{}] {}개의 새 기사를 가져옴", source, articlesToSave.size());

                // 기사 일단 저장 (ID 생성)
                List<Article> initialSavedArticles = articleRepository.saveAll(articlesToSave);

                // 키워드 기반으로 기사별 관심사 매핑
                Map<Article, List<Interest>> articleInterestMap =
                    interestMatchingService.mapArticlesToInterests(initialSavedArticles);

                int articlesWithInterestsCount = 0;
                int totalInterestsAttached = 0;

                // 각 기사에 관심사 연결
                for (Map.Entry<Article, List<Interest>> entry : articleInterestMap.entrySet()) {
                    Article article = entry.getKey();
                    List<Interest> interests = entry.getValue();

                    if (!interests.isEmpty()) {
                        for (Interest interest : interests) {
                            // ArticleInterest 관계 생성 및 추가
                            ArticleInterest articleInterest = new ArticleInterest(article, interest);
                            article.getInterests().add(articleInterest);
                            totalInterestsAttached++;
                        }

                        // 변경사항 저장
                        articleRepository.save(article);
                        articlesWithInterestsCount++;
                    }
                }

                log.info("[{}] 기사 {}개 중 {}개에 총 {}개의 관심사 연결됨",
                    source, initialSavedArticles.size(),
                    articlesWithInterestsCount, totalInterestsAttached);

                savedArticles.addAll(initialSavedArticles);
            }

        } catch (Exception e) {
            log.error("[{}] 기사 가져오기 실패: {}", source, e.getMessage(), e);
        }

        return savedArticles;
    }
}