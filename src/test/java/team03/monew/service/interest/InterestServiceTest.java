package team03.monew.service.interest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.dto.interest.InterestDto;
import team03.monew.dto.interest.InterestFindRequest;
import team03.monew.dto.interest.InterestRegisterRequest;
import team03.monew.dto.interest.InterestUpdateRequest;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Keyword;
import team03.monew.mapper.interest.InterestMapper;
import team03.monew.repository.interest.InterestRepository;
import team03.monew.service.interest.impl.InterestServiceImpl;
import team03.monew.util.exception.interest.EmptyKeywordListException;
import team03.monew.util.exception.interest.InterestAlreadyExistException;

@ExtendWith(MockitoExtension.class)
public class InterestServiceTest {

  @Mock
  private InterestRepository interestRepository;

  @Mock
  private InterestMapper interestMapper;

  @InjectMocks
  private InterestServiceImpl interestService;

  @Nested
  @DisplayName("create() - 관심사 등록 테스트")
  class CreateTest {

    @Test
    @DisplayName("[success] InterestRespository의 save()를 호출하고, 키워드를 포함한 결과를 반환해야 함")
    void successTest() {
      // given
      String name = "test";
      List<String> keywords = List.of("java", "spring");
      InterestRegisterRequest request = new InterestRegisterRequest(name, keywords);

      Interest interest = new Interest(name);
      interest.updateKeywords(keywords);

      given(interestRepository.save(any(Interest.class))).willReturn(interest);
      given(interestMapper.toDto(any(Interest.class), anyBoolean()))
          .willReturn(
              new InterestDto(null, interest.getName(), keywords, interest.getSubscriberCount(),
                  false));

      // when
      InterestDto result = interestService.create(request);

      // then
      verify(interestRepository).save(any(Interest.class));   // 레포지토리의 save()를 호출했는지 확인
      assertThat(result.name()).isEqualTo("test");   // 결과물의 내용이 작성한 것과 동일한지 확인
      assertThat(result.keywords()).hasSize(2);    // 결과물의 키워드 사이즈가 입력한 것과 동일한지 확인
      assertThat(result.keywords()).contains("java", "spring");  // 결과물의 키워드가 작성한 것과 동일한지 확인
    }

    @Test
    @DisplayName("[fail] interest에 키워드가 하나도 없을 경우, InterestRespository의 save()를 호출하지 않아야 함")
    void failKeywordTest() {
      // given
      String name = "test";
      List<String> keywords = new ArrayList<>();
      InterestRegisterRequest request = new InterestRegisterRequest(name, keywords);

      // when & then
      assertThrows(EmptyKeywordListException.class,
          () -> interestService.create(request));  // 해당 종류의 예외가 발생해야 테스트 성공
      verify(interestRepository, never()).save(any(Interest.class));  // 메서드 호출 실패 시 테스트 성공
    }

    @Test
    @DisplayName("[fail] 이미 존재하는 관심사와 80% 이상 유사한 이름의 관심사를 등록하려는 경우, 저장되지 않아야 함")
    void failInterestNameTest() {
      // given
      String existingName = "test1";
      List<String> existingKeywords = List.of("java", "spring");
      Interest existingInterest = new Interest(existingName);
      existingInterest.updateKeywords(existingKeywords);

      given(interestRepository.findAll()).willReturn(List.of(existingInterest));

      String newName = "test2";
      List<String> newKeywords = List.of("java", "spring");
      InterestRegisterRequest request = new InterestRegisterRequest(newName, newKeywords);

      // when & then
      assertThrows(InterestAlreadyExistException.class, () -> interestService.create(request));
      verify(interestRepository, never()).save(any(Interest.class));
    }
  }

  @Nested
  @DisplayName("update() - 관심사 수정 테스트")
  class UpdateTest {

    @Test
    @DisplayName("[success] InterestRespository의 findById()를 호출하고, 새로운 키워드로 교체된 InterestDto가 반환되어야 함")
    void successTest() {
      // given
      UUID interestId = UUID.randomUUID();
      Interest interest = new Interest("test");
      interest.updateKeywords(List.of("java"));
      InterestUpdateRequest request = new InterestUpdateRequest(List.of("java", "spring", "boot"));

      given(interestRepository.findById(interestId)).willReturn(Optional.of(interest));
      given(interestMapper.toDto(any(Interest.class), anyBoolean()))
          .willAnswer(
              Input -> {
                Interest inputInterest = Input.getArgument(0);
                return new InterestDto(
                    interestId.toString(),
                    inputInterest.getName(),
                    inputInterest.getKeywords().stream()
                        .map(Keyword::getName)
                        .toList(),
                    inputInterest.getSubscriberCount(),
                    Input.getArgument(1)
                );
              }
          );

      // when
      InterestDto result = interestService.update(interestId, request, UUID.randomUUID());

      // then
      verify(interestRepository).findById(any(UUID.class));
      assertThat(result.keywords()).hasSize(3);
      assertThat(result.keywords()).contains("java", "spring", "boot");
    }
  }

  @Nested
  @DisplayName("delete() - 관심사 삭제 테스트")
  class DeleteTest {

    @Test
    @DisplayName("[success] InterestRepository의 delete()를 호출해야 함")
    void successTest() {
      // given
      UUID id = UUID.randomUUID();
      Interest interest = new Interest("관심사 삭제 테스트");

      given(interestRepository.findById(id)).willReturn(Optional.of(interest));

      // when
      interestService.delete(id);

      // then
      verify(interestRepository).delete(interest);
    }
  }

  @Nested
  @DisplayName("find() - 관심사 목록 조회 테스트")
  class FindTest {

    @Test
    @DisplayName("[success] CustomInterestRepository의 findInterest()를 호출하고, CursorPageResponse를 반환해야 함")
    void successTest() {
      // given
      InterestFindRequest request = new InterestFindRequest(
          "test",
          "name",
          "asc",
          "cursor",
          String.valueOf(Instant.now()),
          50
      );

      List<Interest> fakeResults = List.of(new Interest("test"));
      given(interestRepository.findInterest(eq(request))).willReturn(fakeResults);
      given(interestRepository.totalCountInterest(eq(request))).willReturn(1L);
      given(interestMapper.toDto(any(), anyBoolean())).willReturn(new InterestDto(
          UUID.randomUUID().toString(),
          "관심사 검색 테스트",
          List.of("키워드1", "키워드2"),
          0,
          false));

      // when
      CursorPageResponse<InterestDto> results = interestService.find(request, UUID.randomUUID());

      // then
      verify(interestRepository).findInterest(eq(request));
      assertThat(results.content()).hasSize(1);
      assertThat(results.content().get(0).name()).isEqualTo("관심사 검색 테스트");
    }
  }
}