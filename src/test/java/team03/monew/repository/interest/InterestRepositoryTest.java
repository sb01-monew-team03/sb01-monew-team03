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
import team03.monew.config.JpaConfig;
import team03.monew.config.QueryDslConfig;
import team03.monew.dto.interest.InterestFindRequest;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Keyword;
import team03.monew.entity.user.User;

@DataJpaTest
@Import({QueryDslConfig.class, JpaConfig.class})
@EntityScan(basePackageClasses = {Interest.class, User.class, Keyword.class}) // 필요한 엔티티만 등록
@AutoConfigureTestDatabase(replace = Replace.NONE)    // 실제 환경에서 테스트
public class InterestRepositoryTest {

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

    @BeforeEach
    void setUp() {
      Interest interest1 = new Interest("관심사1");
      interest1.updateKeywords(List.of("키워드1", "키워드2", "키워드3"));
      Interest saved1 = interestRepository.save(interest1);

      Interest interest2 = new Interest("관심사2");
      interest2.updateKeywords(List.of("키워드1", "키워드5"));
      Interest saved2 = interestRepository.save(interest2);

      Interest interest3 = new Interest("관심사3");
      interest3.updateKeywords(List.of("키워드1"));
      Interest saved3 = interestRepository.save(interest3);

      Interest interest4 = new Interest("관심사4");
      interest4.updateKeywords(List.of("키워드4"));
      Interest saved4 = interestRepository.save(interest4);
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
    @DisplayName("[totalCountInterest()] 검색어에 해당하는 요소 총 개수")
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
  }
}
