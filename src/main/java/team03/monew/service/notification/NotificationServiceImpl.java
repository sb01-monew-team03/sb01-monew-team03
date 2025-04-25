package team03.monew.service.notification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.dto.notification.NotificationDto;
import team03.monew.dto.notification.ResourceType;
import team03.monew.entity.article.Article;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Subscription;
import team03.monew.entity.notification.Notification;
import team03.monew.entity.user.User;
import team03.monew.mapper.notification.NotificationMapper;
import team03.monew.repository.comments.CommentRepository;
import team03.monew.repository.notification.NotificationRepository;
import team03.monew.repository.subscription.SubscriptionRepository;
import team03.monew.repository.user.UserRepository;
import team03.monew.util.exception.comments.CommentNotFoundException;
import team03.monew.util.exception.notification.NotificationNotFoundException;
import team03.monew.util.exception.user.UserNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
  private final NotificationMapper notificationMapper;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final SubscriptionRepository subscriptionRepository;

  // 구독(관심사) 알림 생성
  @Transactional
  @Override
  public List<NotificationDto> createInterestNotification(List<Article> articles) {
    List<NotificationDto> notifications = new ArrayList<>();
    Map<Interest, Integer> interestCount = new HashMap<>();

    articles.stream()
        .flatMap(article -> article.getInterests().stream())
        .forEach(interest -> {
          interestCount.put(interest, interestCount.getOrDefault(interest, 0) + 1);
        });

    interestCount.keySet()
        .forEach(interest -> {
          String content = "[" + interest.getName() + "] 와 관련된 기사가 " + interestCount.get(interest) + "건 등록되었습니다.";
          List<Subscription> subscriptions = subscriptionRepository.findAllByInterest(interest);

          for (Subscription sub : subscriptions) {
            Notification notification = new Notification(sub.getUser(), content, ResourceType.INTEREST, interest.getId());
            notification = notificationRepository.save(notification);
            log.info("구독 알림 생성 완료: notificationId={}", notification.getId());
            notifications.add(notificationMapper.toDto(notification));
          }
        });
    return notifications;
  }

  // 댓글 좋아요 알림 생성
  @Transactional
  @Override
  public NotificationDto createCommentLikeNotification(Comment comment, User user) {
    if (!userRepository.existsById(user.getId())) {
      log.error("존재하지 않는 사용자 ID");
      throw UserNotFoundException.withId(user.getId());
    }

    if (!commentRepository.existsById(comment.getId())) {
      log.error("존재하지 않는 댓글 ID");
      throw CommentNotFoundException.withId(comment.getId());
    }

    String content = user.getNickname() + " 님이 나의 댓글을 좋아합니다.";
    Notification notification = new Notification(user, content, ResourceType.COMMENT, comment.getId());
    notification = notificationRepository.save(notification);
    log.info("댓글 좋아요 알림 생성 완료: notificationId={}", notification.getId());
    return notificationMapper.toDto(notification);
  }

  // 알림 확인 여부 수정
  @Transactional
  @Override
  public void readNotification(UUID id, UUID userId) {
    if (!userRepository.existsById(userId)) {
      log.error("존재하지 않는 사용자 ID");
      throw UserNotFoundException.withId(userId);
    }

    Notification notification = notificationRepository.findById(id).orElseThrow(() -> NotificationNotFoundException.withId(id));
    notification.setConfirmed();
    notificationRepository.save(notification);
    log.info("알림 확인 여부 수정 완료: notificationId={}", notification.getId());
  }

  // 모든 알림 확인
  @Transactional
  @Override
  public void readAllNotification(UUID userId) {
    if (!userRepository.existsById(userId)) {
      log.error("존재하지 않는 사용자 ID");
      throw UserNotFoundException.withId(userId);
    }

    notificationRepository.confirmAllByUserId(userId);
    log.info("모든 알림 읽음");
  }

  // 알림 목록 조회
  @Transactional
  @Override
  public CursorPageResponse<NotificationDto> findAll(String cursor, Instant after, Integer limit, UUID userId) {
    if (!userRepository.existsById(userId)) {
      log.error("존재하지 않는 사용자 ID");
      throw UserNotFoundException.withId(userId);
    }

    try {
      Pageable pageable = PageRequest.of(0, limit, Sort.by(Direction.DESC, "createdAt"));
      Page<Notification> pages = notificationRepository.findPageWithCursor(userId, cursor,
          pageable);

      List<NotificationDto> notificationDtos = pages.getContent()
          .stream()
          .map(notificationMapper::toDto)
          .toList();

      String nextCursor = null;
      Instant nextAfter = null;
      if (pages.hasNext() && !notificationDtos.isEmpty()) {
        Notification lastNotification = pages.getContent().get(pages.getContent().size() - 1);
        nextCursor = lastNotification.getCreatedAt().toString();
        nextAfter = lastNotification.getCreatedAt();
      }

      log.info("알림 목록 조회 완료");
      return new CursorPageResponse<>(
          notificationDtos,
          nextCursor,
          nextAfter,
          notificationDtos.size(),
          pages.getTotalElements(),
          pages.hasNext()
      );
    } catch (IllegalArgumentException e) {
      log.error("알림 페이지네이션 에러 발생");
      throw e;
    }
  }
}
