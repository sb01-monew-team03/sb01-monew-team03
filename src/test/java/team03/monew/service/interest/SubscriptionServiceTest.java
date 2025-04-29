package team03.monew.service.interest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import team03.monew.util.exception.subscription.SubscriptionNotFoundException;

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
      given(interestService.getInterestEntityById(interestId)).willReturn(interest);
      given(subscriptionRepository.existsByUser_IdAndInterest_Id(userId, interestId)).willReturn(false);
      given(subscriptionRepository.save(any(Subscription.class))).willReturn(subscription);
      given(subscriptionMapper.toDto(any(Subscription.class), any())).willReturn(subscriptionDto);
      given(userService.findUserById(userId)).willReturn(user);

      // when
      SubscriptionDto result = subscriptionService.create(userId, interestId);

      // then
      verify(subscriptionRepository).save(any(Subscription.class));
      verify(interestService).updateSubscriberCount(interest, true);
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
      given(interestService.getInterestEntityById(interestId)).willReturn(interest);
      given(subscriptionRepository.existsByUser_IdAndInterest_Id(userId, interestId)).willReturn(true);
      given(userService.findUserById(userId)).willReturn(user);

      // when & then
      assertThrows(SubscriptionAlreadyExistException.class,
          () -> subscriptionService.create(userId, interestId));
      verify(subscriptionRepository, never()).save(any(Subscription.class));
    }
  }

  @Nested
  @DisplayName("delete() - 구독 취소 테스트")
  class DeleteTest {

    @Test
    @DisplayName("[success] SubscriptionRepository의 delete()와 InterestService의 decreaseSubscriberCount()를 호출해야 함")
    void successTest() {
      // given
      UUID userId = UUID.randomUUID();
      UUID interestId = UUID.randomUUID();
      User user = mock(User.class);
      Interest interest = mock(Interest.class);
      Subscription subscription = new Subscription(user, interest);

      // Mocking
      given(interestService.getInterestEntityById(interestId)).willReturn(interest);
      given(subscriptionRepository.findByUser_IdAndInterest_Id(any(UUID.class),
          any(UUID.class))).willReturn(
          Optional.of(subscription));

      // when
      subscriptionService.delete(userId, interestId);

      // then
      verify(subscriptionRepository).delete(any(Subscription.class));
      verify(interestService).updateSubscriberCount(interest, false);
    }

    @Test
    @DisplayName("[fail] 존재하지 않는 구독 정보일 경우 SubscriptionNotFoundException 발생")
    void failTest() {
      // given
      UUID userId = UUID.randomUUID();
      UUID interestId = UUID.randomUUID();
      User user = mock(User.class);
      Interest interest = mock(Interest.class);

      // Mocking
      given(interestService.getInterestEntityById(interestId)).willReturn(interest);
      given(subscriptionRepository.findByUser_IdAndInterest_Id(any(UUID.class),
          any(UUID.class))).willReturn(
          Optional.empty());

      // when & then
      assertThrows(SubscriptionNotFoundException.class,
          () -> subscriptionService.delete(userId, interestId));
      verify(subscriptionRepository, never()).delete(any(Subscription.class));
    }
  }
  
  @Nested
  @DisplayName("existByUserIdAndInterestId() - 구독 여부 확인 테스트")
  class ExistByUserIdAndInterestIdTest {

    @Test
    @DisplayName("[success] 구독 중일 경우 true 반환")
    void successReturnTrueTest() {
      // given
      UUID userId = UUID.randomUUID();
      UUID interestId = UUID.randomUUID();
      given(subscriptionRepository.existsByUser_IdAndInterest_Id(userId, interestId)).willReturn(true);

      // when
      boolean result = subscriptionService.existByUserIdAndInterestId(userId, interestId);

      // then
      assertTrue(result);
      verify(subscriptionRepository).existsByUser_IdAndInterest_Id(userId, interestId);
    }

    @Test
    @DisplayName("[success] 구독하지 않을 경우 false 반환")
    void successReturnFalseTest() {
      // given
      UUID userId = UUID.randomUUID();
      UUID interestId = UUID.randomUUID();
      given(subscriptionRepository.existsByUser_IdAndInterest_Id(userId, interestId)).willReturn(false);

      // when
      boolean result = subscriptionService.existByUserIdAndInterestId(userId, interestId);

      // then
      assertFalse(result);
      verify(subscriptionRepository).existsByUser_IdAndInterest_Id(userId, interestId);
    }
  }
}
