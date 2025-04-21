package team03.monew.repository.interest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Keyword;
import team03.monew.entity.user.User;

@DataJpaTest  // 테스트 끝난 후 롤백
@EntityScan(basePackageClasses = {Interest.class, User.class, Keyword.class}) // 필요한 엔티티만 등록
@AutoConfigureTestDatabase(replace = Replace.NONE)    // 실제 환경에서 테스트
public class InterestRepositoryTest {

  @Autowired
  InterestRepository interestRepository;

  @Autowired
  CustomInterestRepository customInterestRepository;

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

  @Test
  @DisplayName("관심사 목록 조회 테스트")
  void findTest() {
    // given

    // when

    // then

  }
}
