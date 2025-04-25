package team03.monew.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import org.springframework.test.util.ReflectionTestUtils;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.dto.notification.NotificationDto;
import team03.monew.dto.notification.ResourceType;
import team03.monew.entity.article.Article;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Subscription;
import team03.monew.entity.notification.Notification;
import team03.monew.entity.user.User;
import team03.monew.entity.user.User.Role;
import team03.monew.mapper.notification.NotificationMapper;
import team03.monew.repository.comments.CommentRepository;
import team03.monew.repository.notification.NotificationRepository;
import team03.monew.repository.subscription.SubscriptionRepository;
import team03.monew.repository.user.UserRepository;
import team03.monew.util.exception.comments.CommentNotFoundException;
import team03.monew.util.exception.notification.NotificationNotFoundException;
import team03.monew.util.exception.user.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

  @Mock
  private NotificationMapper notificationMapper;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private SubscriptionRepository subscriptionRepository;

  @InjectMocks
  private NotificationServiceImpl notificationService;

  private User user;
  private Comment comment;
  private Interest interest;
  private Notification notification;
  private NotificationDto notificationDto;
  private Article article;
  private Subscription subscription;
  private List<Article> articles;
  private List<Subscription> subscriptions;
  private List<Interest> interests;

  @BeforeEach
  void setUp() {
    user = new User("test", "test@test.com", "qwer1234", Role.USER);

    article = new Article("NAVER", "https://test.com", "test", "test summary", LocalDateTime.now());
    articles = Arrays.asList(article);

    subscription = new Subscription(user, interest);
    subscriptions = List.of(subscription);

    String name = "test";
    List<String> keywords = List.of("java", "spring");
    interest = new Interest(name);
    interest.updateKeywords(keywords);
    interests = new ArrayList<>();
    interests.add(interest);
    article.setInterests(new HashSet<>(interests));

    comment = new Comment("Nice article!", user, article);
  }

  @Test
  @DisplayName("구독(관심사) 알림 생성 테스트")
  void createInterestNotificationTest() {
    given(subscriptionRepository.findAllByInterest(any(Interest.class))).willReturn(subscriptions);

    notification = new Notification(user, "테스트 알림", ResourceType.INTEREST, interest.getId());
    given(notificationRepository.save(any(Notification.class))).willReturn(notification);

    List<NotificationDto> result = notificationService.createInterestNotification(articles);

    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);

    verify(subscriptionRepository).findAllByInterest(any(Interest.class));
    verify(notificationRepository).save(any(Notification.class));
    verify(notificationMapper).toDto(any(Notification.class));
  }

  @Test
  @DisplayName("댓글 좋아요 알림 생성 성공 테스트")
  void createCommentLikeNotificationTest() {
    given(userRepository.existsById(user.getId())).willReturn(true);
    given(commentRepository.existsById(comment.getId())).willReturn(true);

    notification = new Notification(user, user.getNickname() + " 님이 나의 댓글을 좋아합니다.", ResourceType.COMMENT, comment.getId());
    given(notificationRepository.save(any(Notification.class))).willReturn(notification);

    NotificationDto notificationDto = new NotificationDto(UUID.randomUUID(), notification.getCreatedAt(), notification.getUpdatedAt(), notification.isConfirmed(),
        notification.getUser().getId(), notification.getContent(), notification.getType(), notification.getResourceId());
    given(notificationMapper.toDto(any(Notification.class))).willReturn(notificationDto);

    NotificationDto result = notificationService.createCommentLikeNotification(comment, user);

    assertThat(result).isNotNull();

    verify(userRepository).existsById(user.getId());
    verify(commentRepository).existsById(comment.getId());
    verify(notificationRepository).save(any(Notification.class));
    verify(notificationMapper).toDto(any(Notification.class));
  }

  @Test
  @DisplayName("존재하지 않는 사용자로 댓글 좋아요 알림 생성 시 예외 발생")
  void createCommentLikeNotification_UserNotFoundTest() {
    when(userRepository.existsById(user.getId())).thenReturn(false);

    assertThrows(UserNotFoundException.class, () ->
        notificationService.createCommentLikeNotification(comment, user));

    verify(userRepository).existsById(user.getId());
    verify(commentRepository, never()).existsById(any());
    verify(notificationRepository, never()).save(any());
  }

  @Test
  @DisplayName("존재하지 않는 댓글로 좋아요 알림 생성 시 예외 발생")
  void createCommentLikeNotification_CommentNotFoundTest() {
    when(userRepository.existsById(user.getId())).thenReturn(true);
    when(commentRepository.existsById(comment.getId())).thenReturn(false);

    assertThrows(CommentNotFoundException.class, () ->
        notificationService.createCommentLikeNotification(comment, user));

    verify(userRepository).existsById(user.getId());
    verify(commentRepository).existsById(comment.getId());
    verify(notificationRepository, never()).save(any());
  }

  @Test
  @DisplayName("알림 확인 상태 변경 테스트")
  void readNotificationTest() {
    notification = new Notification(user, "테스트 알림", ResourceType.COMMENT, UUID.randomUUID());
    UUID notificationId = UUID.randomUUID();
    ReflectionTestUtils.setField(notification, "id", notificationId);

    when(userRepository.existsById(user.getId())).thenReturn(true);
    when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

    notificationService.readNotification(notificationId, user.getId());

    assertTrue(notification.isConfirmed());

    verify(userRepository).existsById(user.getId());
    verify(notificationRepository).findById(notificationId);
    verify(notificationRepository).save(notification);
  }

  @Test
  @DisplayName("존재하지 않는 사용자로 알림 확인 시 예외 발생")
  void readNotification_UserNotFoundTest() {
    notification = new Notification(user, "테스트 알림", ResourceType.COMMENT, UUID.randomUUID());
    UUID notificationId = UUID.randomUUID();
    ReflectionTestUtils.setField(notification, "id", notificationId);

    when(userRepository.existsById(user.getId())).thenReturn(false);

    assertThrows(UserNotFoundException.class, () ->
        notificationService.readNotification(notification.getId(), user.getId()));

    verify(userRepository).existsById(user.getId());
    verify(notificationRepository, never()).findById(any());
    verify(notificationRepository, never()).save(any());
  }

  @Test
  @DisplayName("존재하지 않는 알림 확인 시 예외 발생")
  void readNotification_NotificationNotFoundTest() {
    notification = new Notification(user, "테스트 알림", ResourceType.COMMENT, UUID.randomUUID());
    UUID notificationId = UUID.randomUUID();
    ReflectionTestUtils.setField(notification, "id", notificationId);

    when(userRepository.existsById(user.getId())).thenReturn(true);
    when(notificationRepository.findById(notification.getId())).thenReturn(Optional.empty());

    assertThrows(NotificationNotFoundException.class, () ->
        notificationService.readNotification(notification.getId(), user.getId()));

    verify(userRepository).existsById(user.getId());
    verify(notificationRepository).findById(notification.getId());
    verify(notificationRepository, never()).save(any());
  }

  @Test
  @DisplayName("모든 알림 확인 테스트")
  void readAllNotificationTest() {
    when(userRepository.existsById(user.getId())).thenReturn(true);
    doNothing().when(notificationRepository).confirmAllByUserId(user.getId());

    notificationService.readAllNotification(user.getId());

    verify(userRepository).existsById(user.getId());
    verify(notificationRepository).confirmAllByUserId(user.getId());
  }

  @Test
  @DisplayName("존재하지 않는 사용자로 모든 알림 확인 시 예외 발생")
  void readAllNotification_UserNotFoundTest() {
    when(userRepository.existsById(user.getId())).thenReturn(false);

    assertThrows(UserNotFoundException.class, () ->
        notificationService.readAllNotification(user.getId()));

    verify(userRepository).existsById(user.getId());
    verify(notificationRepository, never()).confirmAllByUserId(any());
  }

  @Test
  @DisplayName("알림 목록 조회 테스트")
  void findAllTest() {
    String cursor = null;
    Instant after = null;
    int limit = 10;

    notification = new Notification(user, "테스트 알림", ResourceType.COMMENT, UUID.randomUUID());
    UUID notificationId = UUID.randomUUID();
    ReflectionTestUtils.setField(notification, "id", notificationId);

    Instant createdAt = Instant.now();
    ReflectionTestUtils.setField(notification, "createdAt", createdAt);

    NotificationDto notificationDto = new NotificationDto(UUID.randomUUID(), notification.getCreatedAt(), notification.getUpdatedAt(), notification.isConfirmed(),
        notification.getUser().getId(), notification.getContent(), notification.getType(), notification.getResourceId());

    List<Notification> notifications = List.of(notification);
    Page<Notification> page = new PageImpl<>(notifications);

    Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

    when(userRepository.existsById(user.getId())).thenReturn(true);
    when(notificationRepository.findPageWithCursor(eq(user.getId()), eq(cursor), any(Pageable.class))).thenReturn(page);
    when(notificationMapper.toDto(notification)).thenReturn(notificationDto);

    CursorPageResponse<NotificationDto> result = notificationService.findAll(cursor, after, limit, user.getId());

    assertNotNull(result);
    assertEquals(1, result.content().size());
    assertEquals(notificationDto, result.content().get(0));

    assertNull(result.nextCursor());
    assertNull(result.nextAfter());

    assertEquals(1, result.size());
    assertEquals(1, result.totalElements());
    assertFalse(result.hasNext());
  }

  @Test
  @DisplayName("존재하지 않는 사용자로 알림 목록 조회 시 예외 발생")
  void findAll_UserNotFoundTest() {
    String cursor = null;
    Instant after = null;
    int limit = 10;

    notification = new Notification(user, "테스트 알림", ResourceType.COMMENT, UUID.randomUUID());
    UUID notificationId = UUID.randomUUID();
    ReflectionTestUtils.setField(notification, "id", notificationId);

    Instant createdAt = Instant.now();
    ReflectionTestUtils.setField(notification, "createdAt", createdAt);

    when(userRepository.existsById(user.getId())).thenReturn(false);

    assertThrows(UserNotFoundException.class, () ->
        notificationService.findAll(cursor, after, limit, user.getId()));

    verify(userRepository).existsById(user.getId());
    verify(notificationRepository, never()).findPageWithCursor(any(), any(), any());
  }

  @Test
  @DisplayName("알림 목록 조회 중 페이지네이션 에러 발생 테스트")
  void findAll_PaginationErrorTest() {
    String cursor = "invalid-cursor";
    Instant after = null;
    int limit = 10;

    when(userRepository.existsById(user.getId())).thenReturn(true);
    when(notificationRepository.findPageWithCursor(eq(user.getId()), eq(cursor), any(Pageable.class)))
        .thenThrow(new IllegalArgumentException("Invalid cursor format"));

    assertThrows(IllegalArgumentException.class, () ->
        notificationService.findAll(cursor, after, limit, user.getId()));

    verify(userRepository).existsById(user.getId());
    verify(notificationRepository).findPageWithCursor(eq(user.getId()), eq(cursor), any(Pageable.class));
  }
}