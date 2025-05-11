package team03.monew.service.article.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team03.monew.service.article.RssFetcherService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleScheduler {

    private final RssFetcherService rssFetcherService;

    @Scheduled(fixedRate = 3600000)
    public void fetchArticlesHourly() {
        log.info("뉴스 기사 수집을 시작합니다...");
        rssFetcherService.fetchAll();
        log.info("뉴스 기사 수집을 완료했습니다.");
    }
}
