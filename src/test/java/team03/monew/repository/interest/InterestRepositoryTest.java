package team03.monew.repository.interest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import team03.monew.config.JpaConfig;
import team03.monew.config.QueryDslConfig;
import team03.monew.dto.interest.InterestFindRequest;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Keyword;
import team03.monew.entity.user.User;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaConfig.class})
@EntityScan(basePackageClasses = {Interest.class, User.class, Keyword.class}) // 필요한 엔티티만 등록
@EnableJpaRepositories(basePackageClasses = InterestRepository.class)  // 필요한 레포지토리만 등록
@AutoConfigureTestDatabase(replace = Replace.NONE)
class InterestRepositoryTest {

  @Autowired
  InterestRepository interestRepository;

  @Test
  @DisplayName("관심사 저장 테스트")
  void saveTest() {
    // given
    Interest interest = new Interest("관심사 저장 테스트");
    interest.updateKeywords(List.of("키워드1", "키워드2"));

    // when
    Interest result = interestRepository.save(interest);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("관심사 저장 테스트");
    assertThat(result.getKeywords().stream().map(Keyword::getName)).contains("키워드1", "키워드2");
  }

  @Test
  @DisplayName("관심사 수정 테스트")
  void updateTest() {
    // given
    Interest interest = new Interest("관심사 수정 테스트");
    interest.updateKeywords(List.of("키워드1", "키워드2"));
    Interest saved = interestRepository.save(interest);
    saved.updateKeywords(List.of("수정1", "수정2"));

    // when
    Interest result = interestRepository.save(saved);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("관심사 수정 테스트");
    assertThat(result.getKeywords().stream().map(Keyword::getName)).contains("수정1", "수정2");
  }

  @Test
  @DisplayName("관심사 삭제 테스트")
  void deleteTest() {
    // given
    Interest interest = new Interest("관심사 삭제 테스트");
    Interest saved = interestRepository.save(interest);

    // when
    interestRepository.deleteById(saved.getId());

    // then
    assertThat(interestRepository.findById(saved.getId())).isEmpty();
  }

  @Nested
  @DisplayName("관심사 목록 조회 테스트 - CustomInterestRepository")
  class FindTest {

    Interest saved1;
    Interest saved2;
    Interest saved3;
    Interest saved4;

    @BeforeEach
    void setUp() {

      Interest interest3 = new Interest("관심사3");
      interest3.updateKeywords(List.of("키워드1"));
      saved3 = interestRepository.save(interest3);

      Interest interest4 = new Interest("관심사4");
      interest4.updateKeywords(List.of("키워드4"));
      saved4 = interestRepository.save(interest4);

      Interest interest1 = new Interest("관심사1");
      interest1.updateKeywords(List.of("키워드1", "키워드2", "키워드3"));
      saved1 = interestRepository.save(interest1);

      Interest interest2 = new Interest("관심사2");
      interest2.updateKeywords(List.of("키워드1", "키워드5"));
      saved2 = interestRepository.save(interest2);
    }

    @Test
    @DisplayName("[findInterest()] 관심사 이름을 이용한 검색")
    void findInterestInterestNameTest() {
      // given
      InterestFindRequest request = new InterestFindRequest(
          "관심사",
          "name",
          "asc",
          null,
          null,
          2
      );

      // when
      List<Interest> result = interestRepository.findInterest(request);

      // then
      assertThat(result).hasSize(3);
      assertThat(result.get(0).getKeywords()).hasSize(3);
      assertThat(result.get(1).getKeywords().stream()
          .map(Keyword::getName)).contains("키워드1", "키워드5");
    }

    @Test
    @DisplayName("[findInterest()] 키워드 이름을 이용한 검색")
    void findInterestKeywordNameTest() {
      // given
      InterestFindRequest request = new InterestFindRequest(
          "키워드1",
          "name",
          "asc",
          null,
          null,
          2
      );

      // when
      List<Interest> result = interestRepository.findInterest(request);

      // then
      assertThat(result).hasSize(3);
      assertThat(result.get(1).getName()).isEqualTo("관심사2");
      assertThat(result.get(1).getKeywords().stream()
          .map(Keyword::getName)).contains("키워드1", "키워드5");
    }

    @Test
    @DisplayName("[findInterest()] 관심사가 없을 때 전체 목록 조회 가능")
    void findInterestNoKeywordTest() {
      // given
      InterestFindRequest request = new InterestFindRequest(
          "",
          "name",
          "asc",
          null,
          null,
          20
      );

      // when
      List<Interest> result = interestRepository.findInterest(request);

      // then
      assertThat(result).hasSize(4);
    }

    @Test
    @DisplayName("[findInterest()] 이름 오름차순 정렬")
    void findInterestOrderByNameAscTest() {
      // given
      InterestFindRequest request = new InterestFindRequest(
          "",
          "name",
          "asc",
          null,
          null,
          20
      );

      // when
      List<Interest> result = interestRepository.findInterest(request);

      // then
      assertThat(result).hasSize(4);
      assertThat(result.get(0).getName()).isEqualTo("관심사1");
      assertThat(result.get(1).getName()).isEqualTo("관심사2");
      assertThat(result.get(2).getName()).isEqualTo("관심사3");
      assertThat(result.get(3).getName()).isEqualTo("관심사4");
    }

    @Test
    @DisplayName("[findInterest()] 이름 내림차순 정렬")
    void findInterestOrderByNameDescTest() {
      // given
      InterestFindRequest request = new InterestFindRequest(
          "",
          "name",
          "desc",
          null,
          null,
          20
      );

      // when
      List<Interest> result = interestRepository.findInterest(request);

      // then
      assertThat(result).hasSize(4);
      assertThat(result.get(3).getName()).isEqualTo("관심사1");
      assertThat(result.get(2).getName()).isEqualTo("관심사2");
      assertThat(result.get(1).getName()).isEqualTo("관심사3");
      assertThat(result.get(0).getName()).isEqualTo("관심사4");
    }

    @Test
    @DisplayName("[findInterest()] 구독자 수 오름차순 정렬")
    void findInterestOrderBySubscriberCountAscTest() {
      // given
      saved1.increaseSubscribers();
      saved2.increaseSubscribers();
      saved2.increaseSubscribers();
      saved2.increaseSubscribers();
      saved4.increaseSubscribers();
      saved4.increaseSubscribers();

      InterestFindRequest request = new InterestFindRequest(
          "",
          "subscriberCount",
          "asc",
          null,
          null,
          20
      );

      // when
      List<Interest> result = interestRepository.findInterest(request);

      // then
      assertThat(result).hasSize(4);
      assertThat(result.get(0).getName()).isEqualTo("관심사3");
      assertThat(result.get(1).getName()).isEqualTo("관심사1");
      assertThat(result.get(2).getName()).isEqualTo("관심사4");
      assertThat(result.get(3).getName()).isEqualTo("관심사2");
    }

    @Test
    @DisplayName("[findInterest()] 구독자 수 내림차순 정렬")
    void findInterestOrderBySubscriberCountDescTest() {
      // given
      saved1.increaseSubscribers();
      saved2.increaseSubscribers();
      saved2.increaseSubscribers();
      saved2.increaseSubscribers();
      saved4.increaseSubscribers();
      saved4.increaseSubscribers();

      InterestFindRequest request = new InterestFindRequest(
          "",
          "subscriberCount",
          "desc",
          null,
          null,
          20
      );

      // when
      List<Interest> result = interestRepository.findInterest(request);

      // then
      assertThat(result).hasSize(4);
      assertThat(result.get(3).getName()).isEqualTo("관심사3");
      assertThat(result.get(2).getName()).isEqualTo("관심사1");
      assertThat(result.get(1).getName()).isEqualTo("관심사4");
      assertThat(result.get(0).getName()).isEqualTo("관심사2");
    }

    @Test
    @DisplayName("[findInterest()] 이름 커서 기반 페이지네이션")
    void findInterestCursorPaginationNameTest() {

      // given
      InterestFindRequest request = new InterestFindRequest(
          null,
          "name",
          "asc",
          saved3.getName(), // 관심사3
          saved3.getCreatedAt().toString(),
          10
      );

      // when
      List<Interest> result = interestRepository.findInterest(request);

      // then
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getName()).isEqualTo("관심사4");
    }
    
    @Test
    @DisplayName("[findInterest()] 구독자 수 커서 기반 페이지네이션")
    void findInterestCursorPaginationSubscriberCountTest() {

      // given
      // 오름차순 기준 3-1-4-2 순서
      saved1.increaseSubscribers();
      saved2.increaseSubscribers();
      saved2.increaseSubscribers();
      saved2.increaseSubscribers();
      saved4.increaseSubscribers();
      saved4.increaseSubscribers();

      InterestFindRequest request = new InterestFindRequest(
          null,
          "subscriberCount",
          "asc",
          String.valueOf(saved1.getSubscriberCount()), // 관심사1
          saved1.getCreatedAt().toString(),
          10
      );

      // when
      List<Interest> result = interestRepository.findInterest(request);

      // then
      assertThat(result).hasSize(2);
      assertThat(result.get(0).getName()).isEqualTo("관심사4");
      assertThat(result.get(1).getName()).isEqualTo("관심사2");
    }

    @Test
    @DisplayName("[totalCountInterest()] 검색어에 해당하는 요소 총 개수 - 관심사명")
    void totalCountInterestTest() {
      // given
      InterestFindRequest request = new InterestFindRequest(
          "관심사",
          "name",
          "asc",
          null,
          null,
          2
      );

      // when
      long result = interestRepository.totalCountInterest(request);

      // then
      assertThat(result).isEqualTo(4);
    }

    @Test
    @DisplayName("[totalCountInterest()] 검색어에 해당하는 요소 총 개수 - 키워드명")
    void totalCountInterestKeywordSearchTest() {
      // given
      InterestFindRequest request = new InterestFindRequest(
          "키워드5",
          "name",
          "asc",
          null,
          null,
          2
      );

      // when
      long result = interestRepository.totalCountInterest(request);

      // then
      assertThat(result).isEqualTo(1);
    }
  }
}
