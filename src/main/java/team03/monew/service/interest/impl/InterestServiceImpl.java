package team03.monew.service.interest.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.context.annotation.Lazy;
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
import team03.monew.service.interest.InterestService;
import team03.monew.service.interest.SubscriptionService;
import team03.monew.util.exception.interest.EmptyKeywordListException;
import team03.monew.util.exception.interest.InterestAlreadyExistException;
import team03.monew.util.exception.interest.InterestNotFoundException;
import team03.monew.util.exception.interest.OrderByValueException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional    // 모든 public 메서드에 적용됨
public class InterestServiceImpl implements InterestService {

  private final InterestRepository interestRepository;
  private final InterestMapper interestMapper;

  @Lazy   // 순환참조 해결을 위한 지연 로딩
  private final SubscriptionService subscriptionService;

  // 관심사 등록
  @Override
  public InterestDto create(InterestRegisterRequest request) {

    log.debug("관심사 등록 시작: request={}", request);

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
      throw new EmptyKeywordListException();
    }

    // 레포지토리 저장
    interestRepository.save(interest);

    // dto로 변환
    InterestDto interestDto = interestMapper.toDto(interest, false);

    log.info("관심사 등록 완료: interestId={}, interestName={}", interestDto.id(), interestDto.name());

    return interestDto;
  }

  // 관심사 키워드 수정
  @Override
  public InterestDto update(UUID interestId, InterestUpdateRequest request, UUID userId) {

    log.debug("관심사 키워드 수정 시작: interestId={}, request={}, userId={}", interestId, request, userId);

    // 해당 관심사 repository에서 찾기, 예외처리 - 관심사 없는 경우
    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> InterestNotFoundException.withInterestId(interestId));

    // 키워드 수정
    List<String> keywords = request.keywords();
    interest.updateKeywords(keywords);

    // dto로 변환
    InterestDto interestDto = interestMapper.toDto(
        interest,
        subscriptionService.existByUserIdAndInterestId(userId, interestId)
    );

    log.info("관심사 키워드 수정 완료: interestId={}, keywords={}", interestId, interestDto.keywords());

    return interestDto;
  }

  // 관심사 삭제
  @Override
  public void delete(UUID interestId) {

    log.debug("관심사 삭제 시작: interestId={}", interestId);

    // 예외처리 - 해당 관심사가 존재하는지 확인
    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> InterestNotFoundException.withInterestId(interestId));

    // 삭제
    interestRepository.delete(interest);

    log.info("관심사 삭제 완료: interestId={}", interestId);
  }

  // 관심사 목록 조회
  @Override
  @Transactional(readOnly = true)
  public CursorPageResponse<InterestDto> find(InterestFindRequest request, UUID userId) {

    log.debug("관심사 목록 조회 시작: request={}, userId={}", request, userId);

    // 조건에 부합하는 관심사 리스트 가져오기
    List<Interest> interestList = interestRepository.findInterest(request);

    // dto로 넘길 정보 변수 선언
    String nextCursor = null;
    Instant nextAfter = null;
    boolean hasNext = false;

    // 다음 페이지가 있는 경우
    if (interestList.size() > request.limit()) {
      interestList.remove(request.limit());  // 다음 페이지 확인용 마지막 요소 삭제
      Interest lastInterest = interestList.get(request.limit() - 1); // 해당 페이지 마지막 요소
      nextCursor = setNextCursor(lastInterest, request.orderBy());
      nextAfter = lastInterest.getCreatedAt();
      hasNext = true;
    }

    // dto 변환
    List<InterestDto> content = interestList.stream()
        .map(interest -> interestMapper.toDto(interest,
            subscriptionService.existByUserIdAndInterestId(userId, interest.getId())))
        .toList();

    // 커서 페이지네이션 응답용 dto 세팅
    CursorPageResponse<InterestDto> cursorPageResponse = new CursorPageResponse<>(
        content,
        nextCursor,
        nextAfter,
        interestList.size(),
        interestRepository.totalCountInterest(request),
        hasNext
    );

    log.info("관심사 목록 조회 완료: userId={}, 반환 개수={}, hasNext={}, nextCursor={}, totalElements={}",
        userId, content.size(), hasNext, nextCursor, cursorPageResponse.totalElements());

    return cursorPageResponse;
  }

  // 구독자 수 증가
  @Override
  public void increaseSubscriberCount(Interest interest) {

    log.debug("구독자 수 증가 시작: interestId={}, subscriberCount={}", interest.getId(),
        interest.getSubscriberCount());

    interest.increaseSubscribers();

    log.info("구독자 수 증가 완료: interestId={}, subscriberCount={}", interest.getId(),
        interest.getSubscriberCount());
  }

  // 구독자 수 감소
  @Override
  public void decreaseSubscriberCount(Interest interest) {

    log.debug("구독자 수 감소 시작: interestId={}, subscriberCount={}", interest.getId(),
        interest.getSubscriberCount());

    interest.decreaseSubscribers();

    log.info("구독자 수 감소 완료: interestId={}, subscriberCount={}", interest.getId(),
        interest.getSubscriberCount());
  }

  // 관심사 엔티티 반환
  @Override
  @Transactional(readOnly = true)
  public Interest getInterestEntityById(UUID interestId) {

    log.debug("관심사 엔티티 반환 시작: interestId={}", interestId);

    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> InterestNotFoundException.withInterestId(interestId));

    log.info("관심사 엔티티 반환 완료: interestId={}, interestName={}", interest.getId(), interest.getName());

    return interest;
  }


  // 단어 유사성 검사 - 80% 미만일 경우 예외처리
  private void calculateSimilarity(String interestName, String existingInterestName) {
    int maxLength = Math.max(interestName.length(), existingInterestName.length());

    LevenshteinDistance ld = new LevenshteinDistance();

    double result = 0;
    double temp = ld.apply(interestName,
        existingInterestName);   // 두 문자열 간의 최소 편집 거리 계산 - a를 b로 만들기 위해 필요한 최소 삽입/삭제/수정 횟수
    result = (maxLength - temp) / maxLength;

    if (result >= 0.8) {
      throw InterestAlreadyExistException.withInterestName(interestName, existingInterestName);
    }
  }

  private String setNextCursor(Interest lastInterest, String orderBy) {
    switch (orderBy) {
      case "name":
        return lastInterest.getName();
      case "subscriberCount":
        return String.valueOf(lastInterest.getSubscriberCount());
    }
    throw OrderByValueException.withOrderBy(orderBy);
  }
}