package team03.monew.service.interest.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
import team03.monew.util.exception.interest.InterestNotFoundException;
import team03.monew.util.exception.subscription.SubscriptionAlreadyExistException;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionMapper subscriptionMapper;
  private final InterestMapper interestMapper;
  private final InterestService interestService;
  private final UserService userService;

  @Override
  public SubscriptionDto create(UUID userId, UUID interestId) {

    // 필요한 user, interest 세팅
    User user = userService.findUserById(userId);
    Interest interest = interestService.getInterestEntity(interestId)
        .orElseThrow(() -> InterestNotFoundException.withInterestId(interestId));

    // 예외처리 - 구독 중복 방지
    if (subscriptionRepository.existsByUserAndInterest(user, interest)) {
      throw SubscriptionAlreadyExistException.withInterestIdAndUserId(userId, interestId);
    }

    // subscription 생성 및 저장
    Subscription subscription = new Subscription(user, interest);
    subscriptionRepository.save(subscription);

    // interest 구독자 수 증가
    interestService.increaseSubscriberCount(interest);

    // subscriptionDto로 변환 및 반환
    return subscriptionMapper
        .toDto(
            subscription,
            interestMapper.toDto(interest, true)
        );
  }
}
