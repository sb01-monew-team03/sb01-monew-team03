package team03.monew.service.interest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team03.monew.dto.interest.InterestRegisterRequest;
import team03.monew.entity.interest.Interest;
import team03.monew.repository.InterestRepository;

@ExtendWith(MockitoExtension.class)
public class InterestServiceTest {

  @Mock
  private InterestRepository interestRepository;

  @InjectMocks
  private InterestServiceImpl interestService;

  @Nested
  @DisplayName("create()")
  class interestServiceCreateTest {

    @Test
    @DisplayName("[success] InterestRespository의 save()를 호출하고, 키워드를 포함한 결과를 반환해야 함")
    void interestServiceCreateTest() {
      // given
      String name = "test";
      List<String> keywords = List.of("java", "spring");
      InterestRegisterRequest request = new InterestRegisterRequest(name, keywords);

      Interest interest = new Interest(name);
      for (String keyword : keywords) {
        interest.addKeyword(keyword);
      }

      when(interestRepository.save(any(Interest.class))).thenReturn(interest);

      // when
      Interest result = interestService.create(request);

      // then
      verify(interestRepository).save(any(Interest.class));   // 레포지토리의 save()를 호출했는지 확인
      assertThat(result.getName()).isEqualTo("test");   // 결과물의 내용이 작성한 것과 동일한지 확인
      assertThat(result.getKeywords()).hasSize(2);    // 결과물의 키워드 사이즈가 입력한 것과 동일한지 확인
      assertThat(result.getKeywords()).extracting("name")
          .containsExactlyInAnyOrder("java", "spring");  // 결과물의 키워드가 작성한 것과 동일한지 확인
    }

    @Test
    @DisplayName("[fail] interest에 키워드가 하나도 없을 경우, InterestRespository의 save()를 호출하지 않아야 함")
    void interestServiceCreateFailKeywordTest() {
      // given
      String name = "test";
      List<String> keywords = new ArrayList<>();
      InterestRegisterRequest request = new InterestRegisterRequest(name, keywords);

      // when & then
      assertThrows(IllegalArgumentException.class,
          () -> interestService.create(request));  // 해당 종류의 예외가 발생해야 테스트 성공
      verify(interestRepository, never()).save(any(Interest.class));  // 메서드 호출 실패 시 테스트 성공
    }

    @Test
    @DisplayName("[fail] 이미 존재하는 관심사와 80% 이상 유사한 이름의 관심사를 등록하려는 경우, 저장되지 않아야 함")
    void interestServiceCreateFailInterestNameTest() {
      // given
      String existingName = "test1";
      List<String> existingKeywords = List.of("java", "spring");
      Interest existingInterest = new Interest(existingName);
      for (String keyword : existingKeywords) {
        existingInterest.addKeyword(keyword);
      }

      when(interestRepository.findAll()).thenReturn(List.of(existingInterest));

      String newName = "test2";
      List<String> newKeywords = List.of("java", "spring");
      InterestRegisterRequest request = new InterestRegisterRequest(newName, newKeywords);

      // when & then
      assertThrows(IllegalArgumentException.class, () -> interestService.create(request));
      verify(interestRepository, never()).save(any(Interest.class));
    }
  }
}