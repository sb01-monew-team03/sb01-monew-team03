package team03.monew.service.article;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import team03.monew.entity.article.Article;
import team03.monew.repository.article.ArticleRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RssFetcherService {

    private final ArticleRepository articleRepository;

    public void fetchAll() {
        fetchFrom("https://www.hankyung.com/feed/all-news", "Hankyung");
        fetchFrom("https://www.chosun.com/arc/outboundfeeds/rss/?outputType=xml", "Chosun");
        //fetchFrom("http://www.yonhapnewstv.co.kr/browse/feed/", "Yonhap");
    }

    private void fetchFrom(String feedUrl, String source) {
        try (InputStream inputStream = new URL(feedUrl).openStream();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            SyndFeed feed = new SyndFeedInput().build(reader);
            List<SyndEntry> entries = feed.getEntries();

            int savedCount = 0;

            for (SyndEntry entry : entries) {
                String link = entry.getLink();

                if (articleRepository.existsByOriginalLink(link)) {
                    continue;
                }

                LocalDateTime publishedAt = entry.getPublishedDate() != null
                    ? entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    : LocalDateTime.now();

                String summary = entry.getDescription() != null
                    ? Jsoup.parse(entry.getDescription().getValue()).text()
                    : "";

                Article article = new Article(
                    source,
                    link,
                    entry.getTitle(),
                    summary,
                    publishedAt
                );

                articleRepository.save(article);
                savedCount++;
            }

            log.info("[{}] 기사 {}개 저장 완료", source, savedCount);

        } catch (Exception e) {
            log.error("[{}] 기사 오기 실패: {}", source, e.getMessage(), e);
        }
    }
}
