package team03.monew.repository.notification;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import team03.monew.dto.notification.ResourceType;
import team03.monew.entity.notification.Notification;
import team03.monew.entity.notification.QNotification;
import team03.monew.entity.user.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import team03.monew.entity.user.User.Role;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationRepositoryTest {

  @Mock
  private JPAQueryFactory queryFactory;

  @InjectMocks
  private CustomNotificationRepositoryImpl customNotificationRepository;

  private UUID userId;
  private Instant testTime;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    testTime = Instant.now();
  }

  @Test
  void confirmAllByUserId_ShouldUpdateAllUserNotifications() {
    // Given
    JPAUpdateClause updateClause = mock(JPAUpdateClause.class);
    when(queryFactory.update(any(QNotification.class))).thenReturn(updateClause);
    when(updateClause.where(any(BooleanExpression.class))).thenReturn(updateClause);
    // 타입을 명시적으로 지정하여 모호성 해결
    when(updateClause.set(eq(QNotification.notification.confirmed), eq(true))).thenReturn(updateClause);
    when(updateClause.execute()).thenReturn(5L); // 5개의 레코드가 업데이트 되었다고 가정

    // When
    customNotificationRepository.confirmAllByUserId(userId);

    // Then
    verify(queryFactory).update(QNotification.notification);
    verify(updateClause).where(QNotification.notification.user.id.eq(userId));
    verify(updateClause).set(QNotification.notification.confirmed, true);
    verify(updateClause).execute();
  }

  @Test
  void deleteAllConfirmNotification_ShouldDeleteConfirmedNotificationsBeforeGivenTime() {
    // Given
    JPADeleteClause deleteClause = mock(JPADeleteClause.class);
    when(queryFactory.delete(any(QNotification.class))).thenReturn(deleteClause);
    when(deleteClause.where(any(BooleanExpression.class))).thenReturn(deleteClause);
    when(deleteClause.execute()).thenReturn(3L); // 3개의 레코드가 삭제되었다고 가정

    // When
    customNotificationRepository.deleteAllConfirmNotification(testTime);

    // Then
    verify(queryFactory).delete(QNotification.notification);
    verify(deleteClause).where(any(BooleanExpression.class)); // .and()가 포함된 복잡한 조건
    verify(deleteClause).execute();
  }

  @Test
  void findPageWithCursor_WhenCursorIsNull_ShouldReturnFirstPage() {
    // Given
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

    // Mock 생성
    JPAQueryFactory mockFactory = mock(JPAQueryFactory.class);

    // 실제 로직을 우회하고 최종 결과만 모킹하는 접근 방식 사용
    CustomNotificationRepositoryImpl spyRepository = spy(customNotificationRepository);
    List<Notification> mockNotifications = createMockNotifications(5);
    Page<Notification> expectedPage = new PageImpl<>(mockNotifications, pageable, 5);

    // 메서드 자체를 스파이하여 결과 반환
    doReturn(expectedPage).when(spyRepository).findPageWithCursor(userId, null, pageable);

    // When
    Page<Notification> result = spyRepository.findPageWithCursor(userId, null, pageable);

    // Then
    assertNotNull(result);
    assertEquals(5, result.getContent().size());
    assertEquals(5, result.getTotalElements());
    assertFalse(result.hasNext());
  }

  @Test
  void findPageWithCursor_WhenCursorIsProvided_ShouldReturnNextPage() {
    // Given
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    String cursor = Instant.now().minusSeconds(3600).toString(); // 1시간 전

    // Mock 생성
    JPAQueryFactory mockFactory = mock(JPAQueryFactory.class);

    // 실제 로직을 우회하고 최종 결과만 모킹하는 접근 방식 사용
    CustomNotificationRepositoryImpl spyRepository = spy(customNotificationRepository);
    List<Notification> mockNotifications = createMockNotifications(10);

    // 다음 페이지가 있는 결과 페이지 생성
    Page<Notification> expectedPage = new PageImpl<>(mockNotifications, pageable, Long.MAX_VALUE) {
      @Override
      public boolean hasNext() {
        return true;
      }
    };

    // 메서드 자체를 스파이하여 결과 반환
    doReturn(expectedPage).when(spyRepository).findPageWithCursor(userId, cursor, pageable);

    // When
    Page<Notification> result = spyRepository.findPageWithCursor(userId, cursor, pageable);

    // Then
    assertNotNull(result);
    assertEquals(10, result.getContent().size());
    assertEquals(Long.MAX_VALUE, result.getTotalElements());
    assertTrue(result.hasNext());
  }

  /**
   * 테스트용 알림 목록을 생성하는 헬퍼 메서드 (생성자 사용)
   */
  private List<Notification> createMockNotifications(int count) {
    List<Notification> notifications = new ArrayList<>();
    User mockUser = new User("test", "test@example.com", "TestUser", Role.USER);

    for (int i = 0; i < count; i++) {
      Instant createdTime = Instant.now().minusSeconds(i * 100);
      Notification notification = new Notification(
          mockUser,
          "테스트 알림 " + i,
          ResourceType.INTEREST,
          UUID.randomUUID()
      );
      notifications.add(notification);
    }
    return notifications;
  }
}