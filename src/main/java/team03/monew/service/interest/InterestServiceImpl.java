package team03.monew.service.interest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.dto.interest.InterestDto;
import team03.monew.dto.interest.InterestFindRequest;
import team03.monew.dto.interest.InterestRegisterRequest;
import team03.monew.dto.interest.InterestUpdateRequest;
import team03.monew.entity.interest.Interest;
import team03.monew.mapper.interest.InterestMapper;
import team03.monew.repository.interest.InterestRepository;

@Service
@RequiredArgsConstructor
@Transactional    // 모든 public 메서드에 적용됨
public class InterestServiceImpl implements InterestService {

  private final InterestRepository interestRepository;
  private final InterestMapper interestMapper;

  @Override
  public InterestDto create(InterestRegisterRequest request) {

    // request 쪼개기
    String name = request.name();
    List<String> keywords = request.keywords();

    // 예외처리 - name이 80% 이상 유사한 관심사가 있는 경우 관심사 등록 불가
    List<Interest> interests = interestRepository.findAll();
    for (Interest interest : interests) {
      calculateSimilarity(name, interest.getName());
    }

    // Interest 생성
    Interest interest = new Interest(name);

    // interest에 keyword 추가
    interest.updateKeywords(keywords);

    // 예외처리 - 키워드 하나 이상 있어야 함
    if (interest.getKeywords().isEmpty()) {
      throw new IllegalArgumentException("키워드는 최소 1개 이상이어야 합니다.");
    }

    // 레포지토리 저장
    interestRepository.save(interest);

    // 결과물 반환
    return interestMapper.toDto(interest, false);
  }

  @Override
  public InterestDto update(UUID interestId, InterestUpdateRequest request) {

    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> new IllegalArgumentException("해당 관심사가 존재하지 않습니다."));
    List<String> keywords = request.keywords();

    interest.updateKeywords(keywords);

    // TODO: 구독 구현 후 subscribedByMe 수정
    return interestMapper.toDto(interest, true);
  }

  @Override
  public void deleteById(UUID interestId) {
    interestRepository.deleteById(interestId);
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponse<InterestDto> find(InterestFindRequest request) {

    List<Interest> interestList = interestRepository.findInterest(request);
    String nextCursor = null;
    Instant nextAfter = null;
    boolean hasNext = false;

    if (interestList.size() > request.limit()) {  // 다음 페이지가 있는 경우
      interestList.remove(request.limit());  // 다음 페이지 확인용 마지막 요소 삭제
      Interest lastInterest = interestList.get(request.limit() - 1); // 해당 페이지 마지막 요소
      nextCursor = setNextCursor(lastInterest, request.orderBy());
      nextAfter = lastInterest.getCreatedAt();
    }
    
    List<InterestDto> content = interestList.stream()
        // TODO: 구독 구현 후 subscribedByMe 수정
        .map(interest -> interestMapper.toDto(interest, false))
        .toList();

    return new CursorPageResponse<>(
        content,
        nextCursor,
        nextAfter,
        interestList.size(),
        interestRepository.totalCountInterest(request),
        hasNext
    );
  }


  // 단어 유사성 검사 - 80% 미만일 경우 예외처리
  private void calculateSimilarity(String a, String b) {
    int maxLength = Math.max(a.length(), b.length());

    LevenshteinDistance ld = new LevenshteinDistance();

    double result = 0;
    double temp = ld.apply(a, b);   // 두 문자열 간의 최소 편집 거리 계산 - a를 b로 만들기 위해 필요한 최소 삽입/삭제/수정 횟수
    result = (maxLength - temp) / maxLength;

    if (result >= 0.8) {
      throw new IllegalArgumentException(
          "작성하신 관심사 " + a + "가 이미 존재하는 관심사인 " + b + "와 80% 이상(" + (result * 100) + "%) 유사합니다.");
    }
  }

  private String setNextCursor(Interest lastInterest, String orderBy) {
    switch (orderBy) {
      case "name":
        return lastInterest.getName();
      case "subscriberCount":
        return String.valueOf(lastInterest.getSubscriberCount());
    }
    throw new IllegalArgumentException("orderBy는 name, subscriberCount만 가능합니다.");
  }
}