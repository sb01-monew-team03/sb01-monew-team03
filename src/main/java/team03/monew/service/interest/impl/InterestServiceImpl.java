package team03.monew.service.interest.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
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
import team03.monew.dto.interest.PaginationDto;
import team03.monew.entity.interest.Interest;
import team03.monew.mapper.interest.InterestMapper;
import team03.monew.repository.interest.InterestRepository;
import team03.monew.service.interest.InterestReader;
import team03.monew.service.interest.InterestService;
import team03.monew.service.interest.SubscriptionService;
import team03.monew.util.exception.interest.EmptyKeywordListException;
import team03.monew.util.exception.interest.ExcessiveRetryException;
import team03.monew.util.exception.interest.InterestAlreadyExistException;
import team03.monew.util.exception.interest.InterestNotFoundException;
import team03.monew.util.exception.interest.OrderByValueException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional    // 모든 public 메서드에 적용됨
public class InterestServiceImpl implements InterestService {


  public static final int LOCK_WAIT_TIME_MS = 100;  // 낙관적 락 충돌 시 대기 시간
  public static final int MAX_RETRY_COUNT = 5;  // 낙관적 락 최대 시도 횟수

  private final InterestRepository interestRepository;
  private final InterestMapper interestMapper;
  private final SubscriptionService subscriptionService;
  private final InterestReader interestReader;
  private final EntityManager entityManager;

  // 관심사 등록
  @Override
  public InterestDto create(InterestRegisterRequest request) {

    log.debug("[create] 관심사 등록 시작: request={}", request);

    // request 쪼개기
    String name = request.name();
    List<String> keywords = request.keywords();

    // 예외처리 - name이 80% 이상 유사한 관심사가 있는 경우 관심사 등록 불가
    checkSimilarity(name);

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

    log.info("[create] 관심사 등록 완료: interestId={}, interestName={}", interestDto.id(),
        interestDto.name());

    return interestDto;
  }

  // 관심사 키워드 수정
  @Override
  public InterestDto update(UUID interestId, InterestUpdateRequest request, UUID userId) {

    log.debug("[update] 관심사 키워드 수정 시작: interestId={}, request={}, userId={}", interestId, request,
        userId);

    // 해당 관심사 검색
    Interest interest = interestReader.getInterestEntityById(interestId);

    // 키워드 수정
    List<String> keywords = request.keywords();
    setKeywords(interest);
    interest.updateKeywords(keywords);

    // dto로 변환
    InterestDto interestDto = interestMapper.toDto(
        interest,
        subscriptionService.existByUserIdAndInterestId(userId, interestId)
    );

    log.info("[update] 관심사 키워드 수정 완료: interestId={}, keywords={}", interestDto.id(),
        interestDto.keywords());

    return interestDto;
  }

  // 관심사 삭제
  @Override
  public void delete(UUID interestId) {

    log.debug("[delete] 관심사 삭제 시작: interestId={}", interestId);

    // 예외처리 - 해당 관심사가 존재하는지 확인
    Interest interest = interestReader.getInterestEntityById(interestId);

    // 삭제
    interestRepository.delete(interest);

    log.info("[delete] 관심사 삭제 완료: interestId={}", interest.getId());
  }

  // 관심사 목록 조회
  @Override
  @Transactional(readOnly = true)
  public CursorPageResponse<InterestDto> find(InterestFindRequest request, UUID userId) {

    log.debug("[find] 관심사 목록 조회 시작: request={}, userId={}", request, userId);

    // 조건에 부합하는 관심사 리스트 가져오기
    List<Interest> interestList = interestRepository.findInterest(request);

    // 다음 페이지에 필요한 정보(nextCursor, nextAfter, hasNext) 세팅
    PaginationDto paginationDto = setPaginationDto(interestList, request, userId);

    // 커서 페이지네이션 응답용 dto 세팅
    CursorPageResponse<InterestDto> cursorPageResponse = new CursorPageResponse<>(
        paginationDto.content(),
        paginationDto.nextCursor(),
        paginationDto.nextAfter(),
        paginationDto.size(),
        interestRepository.totalCountInterest(request),
        paginationDto.hasNext()
    );

    log.info(
        "[find] 관심사 목록 조회 완료: userId={}, 반환 개수={}, hasNext={}, nextCursor={}, totalElements={}",
        userId, paginationDto.content().size(), paginationDto.hasNext(), paginationDto.nextCursor(),
        cursorPageResponse.totalElements());

    return cursorPageResponse;
  }

  // 구독자 수 변경
  @Override
  public void updateSubscriberCount(Interest interest, boolean increase) {

    log.debug("[updateSubscriberCount] 구독자 수 변경 시작: interestId={}, subscriberCount={}, 구독자수 변경={}",
        interest.getId(),
        interest.getSubscriberCount(),
        increase ? "증가" : "감소");

    boolean update = false;
    int count = 0;

    while (!update) {
      try {
        if (increase) {
          interest.increaseSubscribers();
        } else {
          interest.decreaseSubscribers();
        }
        update = true;
      } catch (OptimisticLockException e) {

        log.warn("[OptimisticLockException] 구독자 수 변경 충돌: interestId={}, subscriberCount={}",
            interest.getId(), interest.getSubscriberCount(), e);

        if (count > MAX_RETRY_COUNT) {
          throw new ExcessiveRetryException();
        }

        count++;
        handleOptimisticLockException();
      }
    }

    log.info("[updateSubscriberCount] 구독자 수 변경 완료: interestId={}, subscriberCount={}, 구독자수 변경={}",
        interest.getId(),
        interest.getSubscriberCount(),
        increase ? "증가" : "감소");
  }


  // 단어 유사성 검사
  private void checkSimilarity(String name) {

    List<Interest> interests = interestRepository.findAll();

    for (Interest interest : interests) {
      log.trace("유사성 비교 중: 입력 name={}, 기존 name={}", name, interest.getName());
      calculateSimilarity(name, interest.getName());
    }
  }

  // 단어 유사성 계산 - 80% 미만일 경우 예외처리
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

  // 키워드 수정 세팅
  private void setKeywords(Interest interest) {
    interest.getKeywords().clear();
    entityManager.flush();  // 키워드 삭제 후 강제로 db에 저장
  }

  // 다음 페이지에 필요한 정보 세팅
  private PaginationDto setPaginationDto(List<Interest> interestList, InterestFindRequest request, UUID userId) {

    // 다음 페이지가 없는 경우
    if (interestList.size() <= request.limit()) {
      return new PaginationDto(null, null, null, false, interestList.size());
    }

    // 다음 페이지가 있는 경우
    List<Interest> paginatedList = interestList.subList(0,
        request.limit());  // 마지막 요소를 제외한 list 새로 만듦
    Interest lastInterest = paginatedList.get(paginatedList.size() - 1); // 해당 페이지 마지막 요소
    String nextCursor = setNextCursor(lastInterest, request.orderBy());
    Instant nextAfter = lastInterest.getCreatedAt();
    boolean hasNext = true;

    List<InterestDto> content = paginatedList.stream()
        .map(interest -> interestMapper.toDto(interest,
            subscriptionService.existByUserIdAndInterestId(userId, interest.getId())))
        .toList();

    return new PaginationDto(content, nextCursor, nextAfter, hasNext, paginatedList.size());
  }

  // 커서 세팅
  private String setNextCursor(Interest lastInterest, String orderBy) {
    switch (orderBy) {
      case "name":
        return lastInterest.getName();
      case "subscriberCount":
        return String.valueOf(lastInterest.getSubscriberCount());
    }
    throw OrderByValueException.withOrderBy(orderBy);
  }

  // 낙관적 락 충돌 시 처리 로직
  private void handleOptimisticLockException() {

    try {
      Thread.sleep(LOCK_WAIT_TIME_MS);  // 0.1초 대기
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }
}