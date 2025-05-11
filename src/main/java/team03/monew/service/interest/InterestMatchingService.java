package team03.monew.service.interest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.entity.article.Article;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Keyword;
import team03.monew.repository.interest.interest.InterestRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestMatchingService {

  private final InterestRepository interestRepository;

  /**
   * 기사의 제목과 요약에서 키워드를 검색하여 관련 관심사를 매핑합니다.
   *
   * @param articles 분석할 기사 목록
   * @return 기사를 키로, 해당 기사에 매칭된 관심사 목록을 값으로 하는 맵
   */
  @Transactional(readOnly = true)
  public Map<Article, List<Interest>> mapArticlesToInterests(List<Article> articles) {
    log.info("{}개의 기사에 대한 키워드 기반 관심사 매핑 시작", articles.size());

    // 1. 모든 관심사와 키워드 조회
    List<Interest> allInterests = interestRepository.findAll();

    if (allInterests.isEmpty()) {
      log.warn("등록된 관심사가 없습니다.");
      return Map.of();
    }

    // 2. 각 기사별로 관련 관심사 매핑
    Map<Article, List<Interest>> articleInterestMap = new HashMap<>();

    for (Article article : articles) {
      List<Interest> matchedInterests = new ArrayList<>();

      String title = article.getTitle() != null ? article.getTitle().toLowerCase() : "";
      String summary = article.getSummary() != null ? article.getSummary().toLowerCase() : "";
      String fullText = title + " " + summary;

      for (Interest interest : allInterests) {
        boolean matched = false;

        // 관심사 이름이 직접 포함된 경우
        if (fullText.contains(interest.getName().toLowerCase())) {
          matched = true;
          log.debug("기사 ID: {} - 관심사 이름 '{}' 직접 매칭됨",
              article.getId(), interest.getName());
        }

        // 키워드 검색
        if (!matched && interest.getKeywords() != null) {
          for (Keyword keyword : interest.getKeywords()) {
            if (keyword.getName() != null && fullText.contains(keyword.getName().toLowerCase())) {
              matched = true;
              log.debug("기사 ID: {} - 관심사 '{}'의 키워드 '{}' 매칭됨",
                  article.getId(), interest.getName(), keyword.getName());
              break;
            }
          }
        }

        if (matched && !matchedInterests.contains(interest)) {
          matchedInterests.add(interest);
        }
      }

      if (!matchedInterests.isEmpty()) {
        articleInterestMap.put(article, matchedInterests);
        log.info("기사 ID: {} (제목: {})에 {}개의 관심사 매칭됨",
            article.getId(), article.getTitle(), matchedInterests.size());

        // 매칭된 관심사 이름 로깅
        log.info("매칭된 관심사: {}", matchedInterests.stream()
            .map(Interest::getName)
            .toList());
      }
    }

    log.info("매핑 완료: {}개 기사 중 {}개에 관심사 매칭됨",
        articles.size(), articleInterestMap.size());

    return articleInterestMap;
  }
}