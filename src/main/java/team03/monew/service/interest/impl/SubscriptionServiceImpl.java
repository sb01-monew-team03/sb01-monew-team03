package team03.monew.service.interest.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.dto.interest.SubscriptionDto;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Subscription;
import team03.monew.entity.user.User;
import team03.monew.mapper.interest.InterestMapper;
import team03.monew.mapper.interest.SubscriptionMapper;
import team03.monew.repository.interest.SubscriptionRepository;
import team03.monew.service.interest.InterestService;
import team03.monew.service.interest.SubscriptionService;
import team03.monew.service.user.UserService;
import team03.monew.util.exception.subscription.SubscriptionAlreadyExistException;
import team03.monew.util.exception.subscription.SubscriptionNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionMapper subscriptionMapper;
  private final InterestMapper interestMapper;
  private final UserService userService;
  private final InterestService interestService;

  // 구독
  @Override
  public SubscriptionDto create(UUID userId, UUID interestId) {

    log.debug("[create] 구독 시작: userId={}, interestId={}", userId, interestId);

    // 필요한 user, interest 세팅
    User user = userService.findUserById(userId);
    Interest interest = interestService.getInterestEntityById(interestId);

    // 예외처리 - 구독 중복 방지
    if (existByUserIdAndInterestId(userId, interestId)) {
      throw SubscriptionAlreadyExistException.withInterestIdAndUserId(userId, interestId);
    }

    // subscription 생성 및 저장
    Subscription subscription = new Subscription(user, interest);
    subscriptionRepository.save(subscription);

    // interest 구독자 수 증가
    interestService.updateSubscriberCount(interest, true);

    // dto 변환
    SubscriptionDto subscriptionDto = subscriptionMapper
        .toDto(
            subscription,
            interestMapper.toDto(interest, true)
        );

    log.info("[create] 구독 완료: subscriptionId={}, subscribed_at={}", subscription.getId(), subscription.getCreatedAt());

    return subscriptionDto;
  }

  // 구독 취소
  @Override
  public void delete(UUID userId, UUID interestId) {

    log.debug("[delete] 구독 취소 시작: userId={}, interestId={}", userId, interestId);

    // 필요한 user, interest 세팅
    Interest interest = interestService.getInterestEntityById(interestId);

    // subscription 가져오기, 예외처리 - 해당 구독 정보 없는 경우
    Subscription subscription = subscriptionRepository.findByUser_IdAndInterest_Id(userId,
            interestId)
        .orElseThrow(
            () -> SubscriptionNotFoundException.withUserIdAndInterestId(userId, interestId));

    // 삭제
    subscriptionRepository.delete(subscription);

    // 구독자 수 감소
    interestService.updateSubscriberCount(interest, false);

    log.info("[delete] 구독 취소 완료: subscriptionId={}, userId={}, interestId={}", subscription.getId(), userId, interestId);
  }

  // 구독 여부 확인
  @Override
  public boolean existByUserIdAndInterestId(UUID userId, UUID interestId) {

    boolean isExisted = subscriptionRepository.existsByUser_IdAndInterest_Id(userId, interestId);

    log.info("[existByUserIdAndInterestId] 구독 여부 확인: userId={}, interestId={}, isSubscribed={}", userId, interestId, isExisted);

    return isExisted;
  }
}
