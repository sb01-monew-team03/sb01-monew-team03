package team03.monew.service.article;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import jakarta.transaction.Transactional;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team03.monew.entity.article.Article;
import team03.monew.entity.interest.Interest;
import team03.monew.repository.article.ArticleRepository;
import team03.monew.repository.interest.interest.InterestRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RssFetcherService {

    private final ArticleRepository articleRepository;
    private final InterestRepository interestRepository;

    public void fetchAll() {
        fetchFrom("https://www.hankyung.com/feed/all-news", "HANKYUNG");
        fetchFrom("https://www.chosun.com/arc/outboundfeeds/rss/?outputType=xml", "CHOSUN");
        //fetchFrom("http://www.yonhapnewstv.co.kr/browse/feed/", "Yonhap");
    }

    private void fetchFrom(String feedUrl, String source) {
        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(feedUrl)));
            List<SyndEntry> entries = feed.getEntries();

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

                List<Interest> allInterests = interestRepository.findAll();
                Set<Interest> matchedInterests = allInterests.stream()
                    .filter(interest -> interest.getKeywords().stream()
                        .anyMatch(keyword -> entry.getTitle().contains(keyword.getName()) ||
                            (entry.getDescription() != null && entry.getDescription().getValue()
                                .contains(keyword.getName()))
                        )
                    ).collect(Collectors.toSet());

                log.info("✅ 기사에 관심사가 새로 매칭되었습니다: {}",
                    matchedInterests.stream().map(Interest::getName).toList());
                article.updateInterests(matchedInterests);
                articleRepository.save(article);
            }

            log.info("[{}] 기사 {}개를 불러왔습니다", source, entries.size());

        } catch (Exception e) {
            log.error("[{}] 기사 오기 실패: {}", source, e.getMessage(), e);
        }
    }
}
