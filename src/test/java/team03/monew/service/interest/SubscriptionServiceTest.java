package team03.monew.service.interest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team03.monew.dto.interest.SubscriptionDto;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Subscription;
import team03.monew.entity.user.User;
import team03.monew.mapper.interest.InterestMapper;
import team03.monew.mapper.interest.SubscriptionMapper;
import team03.monew.repository.interest.SubscriptionRepository;
import team03.monew.service.interest.impl.SubscriptionServiceImpl;
import team03.monew.service.user.UserService;
import team03.monew.util.exception.subscription.SubscriptionAlreadyExistException;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

  @Mock
  private SubscriptionRepository subscriptionRepository;

  @Mock
  private SubscriptionMapper subscriptionMapper;

  @Mock
  private InterestMapper interestMapper;

  @Mock
  private InterestService interestService;

  @Mock
  private UserService userService;

  @InjectMocks
  private SubscriptionServiceImpl subscriptionService;

  @Nested
  @DisplayName("create() - 구독 테스트")
  class CreateTest {

    // TODO: 현재 깨짐, User 엔티티 반환 메서드 생기면 다시 확인
    @Test
    @DisplayName("[success] SubscriptionRepository의 save()와 InterestService의 increaseSubscriberCount()를 호출하고, SubscriptionDto를 반환해야 함")
    void successTest() {
      // given
      UUID userId = UUID.randomUUID();
      UUID interestId = UUID.randomUUID();
      User user = mock(User.class);
      Interest interest = mock(Interest.class);
      Subscription subscription = new Subscription(user, interest);
      SubscriptionDto subscriptionDto = mock(SubscriptionDto.class);

      // Mocking
      given(interestService.getInterestEntity(interestId)).willReturn(Optional.of(interest));
      given(subscriptionRepository.existsByUserAndInterest(user, interest)).willReturn(false);
      given(subscriptionRepository.save(any(Subscription.class))).willReturn(subscription);
      given(subscriptionMapper.toDto(any(Subscription.class), any())).willReturn(subscriptionDto);
      given(userService.findUserById(userId)).willReturn(user);

      // when
      SubscriptionDto result = subscriptionService.create(userId, interestId);

      // then
      verify(subscriptionRepository).save(any(Subscription.class));
      verify(interestService).increaseSubscriberCount(interest);
      assertThat(result).isEqualTo(subscriptionDto);
    }

    @Test
    @DisplayName("[fail] 이미 구독이 존재하는 경우 SubscriptionAlreadyExistException 발생")
    void failTest() {
      // given
      UUID userId = UUID.randomUUID();
      UUID interestId = UUID.randomUUID();
      User user = mock(User.class);
      Interest interest = mock(Interest.class);

      // Mocking
      given(interestService.getInterestEntity(interestId)).willReturn(Optional.of(interest));
      given(subscriptionRepository.existsByUserAndInterest(user, interest)).willReturn(true);
      given(userService.findUserById(userId)).willReturn(user);

      // when & then
      assertThrows(SubscriptionAlreadyExistException.class,
          () -> subscriptionService.create(userId, interestId));
      verify(subscriptionRepository, never()).save(any(Subscription.class));
    }
  }
}
