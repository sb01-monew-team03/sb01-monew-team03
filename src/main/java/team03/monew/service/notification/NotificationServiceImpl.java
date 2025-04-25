package team03.monew.service.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.dto.notification.NotificationDto;
import team03.monew.dto.notification.NotificationFindRequest;
import team03.monew.dto.notification.ResourceType;
import team03.monew.entity.article.Article;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Subscription;
import team03.monew.entity.notification.Notification;
import team03.monew.entity.user.User;
import team03.monew.mapper.notification.NotificationMapper;
import team03.monew.repository.comments.CommentRepository;
import team03.monew.repository.interest.SubscriptionRepository;
import team03.monew.repository.notification.NotificationRepository;
import team03.monew.repository.user.UserRepository;
import team03.monew.util.exception.comment.CommentNotFoundException;
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
            notifications.add(notificationMapper.toDto(notification));
          }
        });

    return notifications;
  }

  @Transactional
  @Override
  public NotificationDto createCommentLikeNotification(Comment comment, User user) {
    if (!userRepository.existsById(user.getId())) {
      throw UserNotFoundException.withId(user.getId());
    }

    if (!commentRepository.existsById(comment.getId())) {
      throw CommentNotFoundException.withId(comment.getId());
    }

    String content = user.getNickname() + " 님이 나의 댓글을 좋아합니다.";
    Notification notification = new Notification(user, content, ResourceType.COMMENT, comment.getId());
    notification = notificationRepository.save(notification);
    return notificationMapper.toDto(notification);
  }

  @Transactional
  @Override
  public void readNotification(UUID id, UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw UserNotFoundException.withId(userId);
    }

    Notification notification = notificationRepository.findById(id).orElseThrow(() -> NotificationNotFoundException.withId(id));
    notification.confirmed();
    notificationRepository.save(notification);
  }

  @Transactional
  @Override
  public void readAllNotification(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw UserNotFoundException.withId(userId);
    }

    notificationRepository.confirmAllByUserId(userId);
  }

  // TODO: findAll()
  public CursorPageResponse<NotificationDto> findAll(NotificationFindRequest request) {

  return null;
  }

}
