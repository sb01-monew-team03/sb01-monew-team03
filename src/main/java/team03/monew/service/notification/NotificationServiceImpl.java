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
import team03.monew.entity.article.ArticleInterest;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Subscription;
import team03.monew.entity.notification.Notification;
import team03.monew.entity.user.User;
import team03.monew.mapper.notification.NotificationMapper;
import team03.monew.repository.comments.CommentRepository;
import team03.monew.repository.interest.interest.InterestRepository;
import team03.monew.repository.interest.subscription.SubscriptionRepository;
import team03.monew.repository.notification.NotificationRepository;
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
  private final InterestRepository interestRepository;

// 구독(관심사) 알림 생성
  @Transactional
  @Override
  public List<NotificationDto> createInterestNotification(List<Article> articles) {
    List<NotificationDto> notifications = new ArrayList<>();

    // 1. 모든 기사에서 관심사 추출 및 카운팅
    Map<UUID, Integer> interestCountMap = new HashMap<>();
    Map<UUID, String> interestNameMap = new HashMap<>();

    for (Article article : articles) {
      for (ArticleInterest articleInterest : article.getInterests()) {
        Interest interest = articleInterest.getInterest();
        UUID interestId = interest.getId();

        // 관심사 ID별 카운트 증가
        interestCountMap.put(interestId, interestCountMap.getOrDefault(interestId, 0) + 1);

        // 관심사 이름 맵핑 저장 (로깅용)
        interestNameMap.put(interestId, interest.getName());
      }
    }

    // 카운팅 로그
    interestCountMap.forEach((interestId, count) -> {
      log.info("관심사 '{}' (ID: {})에 관련된 새 기사 {}건",
          interestNameMap.get(interestId), interestId, count);
    });

    // 2. 각 관심사별로 구독자 찾아서 알림 생성
    for (Map.Entry<UUID, Integer> entry : interestCountMap.entrySet()) {
      UUID interestId = entry.getKey();
      int count = entry.getValue();
      String interestName = interestNameMap.get(interestId);

      // 관심사 조회
      Interest interest;
      try {
        interest = interestRepository.findById(interestId)
            .orElseThrow(() -> new IllegalStateException("관심사를 찾을 수 없음: " + interestId));
      } catch (Exception e) {
        log.error("관심사 조회 중 오류 발생: {}", e.getMessage(), e);
        continue;
      }

      // 알림 내용 생성
      String content = "[" + interestName + "] 와 관련된 기사가 " + count + "건 등록되었습니다.";

      // 해당 관심사의 구독자 조회
      List<Subscription> subscriptions;
      try {
        subscriptions = subscriptionRepository.findAllByInterest(interest);
        log.info("관심사 '{}' 구독자 수: {}", interestName, subscriptions.size());
      } catch (Exception e) {
        log.error("구독자 조회 중 오류 발생: {}", e.getMessage(), e);
        continue;
      }

      // 각 구독자에게 알림 생성
      for (Subscription subscription : subscriptions) {
        try {
          User user = subscription.getUser();

          // null 체크
          if (user == null) {
            log.warn("구독 ID: {}에 연결된 사용자가 없습니다", subscription.getId());
            continue;
          }

          log.info("사용자 ID: {}에게 '{}' 관련 알림 생성 시도", user.getId(), interestName);

          // 알림 엔티티 생성 및 저장
          Notification notification = new Notification(user, content, ResourceType.INTEREST, interestId);
          notification = notificationRepository.save(notification);

          if (notification.getId() != null) {
            log.info("알림 생성 성공: ID={}, 사용자={}, 관심사={}, 기사수={}",
                notification.getId(), user.getId(), interestName, count);
            notifications.add(notificationMapper.toDto(notification));
          } else {
            log.error("알림 저장 실패: 사용자={}, 관심사={}", user.getId(), interestName);
          }
        } catch (Exception e) {
          log.error("알림 생성 중 예외 발생: {}", e.getMessage(), e);
        }
      }
    }

    log.info("총 {}개의 알림이 생성되었습니다", notifications.size());
    return notifications;
  }

  // 댓글 좋아요 알림 생성
  @Transactional
  @Override
  public NotificationDto createCommentLikeNotification(Comment comment, User liker) {
    if (!userRepository.existsById(liker.getId())) {
      log.error("존재하지 않는 사용자 ID");
      throw UserNotFoundException.withId(liker.getId());
    }

    if (!commentRepository.existsById(comment.getId())) {
      log.error("존재하지 않는 댓글 ID");
      throw CommentNotFoundException.withId(comment.getId());
    }

    User commentOwner = comment.getUser();
    String content = liker.getNickname() + " 님이 나의 댓글을 좋아합니다.";
    Notification notification = new Notification(commentOwner, content, ResourceType.COMMENT, comment.getId());
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
  public CursorPageResponse<NotificationDto> findAll(UUID userId, String cursor, Instant after, Integer limit) {
    if (!userRepository.existsById(userId)) {
      log.error("존재하지 않는 사용자 ID");
      throw UserNotFoundException.withId(userId);
    }

    try {
      Pageable pageable = PageRequest.of(0, limit, Sort.by(Direction.ASC, "createdAt"));
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
