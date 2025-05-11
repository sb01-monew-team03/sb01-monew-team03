package team03.monew.event.activity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.document.ActivityDocument;
import team03.monew.document.ActivityDocument.CommentData;
import team03.monew.document.ActivityDocument.SubscriptionData;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.interest.Subscription;
import team03.monew.entity.user.User;
import team03.monew.repository.activity.ActivityRepository;
import team03.monew.repository.user.UserRepository;
import team03.monew.util.exception.user.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class ActivityEventHandler {

  private final ActivityRepository activityRepository;
  private final UserRepository userRepository;

  @Transactional
  public void handleCommentCreated(Comment comment) {
    ActivityDocument activity = activityRepository.findByUserId(comment.getUser().getId())
        .orElseGet(() -> createNewActivityDocument(comment.getUser().getId()));

    CommentData commentData = CommentData.builder()
        .commentId(comment.getId())
        .content(comment.getContent())
        .userId(comment.getUser().getId())
        .userNickname(comment.getUser() != null ? comment.getUser().getNickname() : null)
        .articleId(comment.getArticle().getId())
        .articleTitle(comment.getArticle() != null ? comment.getArticle().getTitle() : null)
        .createdAt(comment.getCreatedAt())
        .likeCount(comment.getLikeCount())
        .build();

    activity.getComments().add(commentData);
    activityRepository.save(activity);
  }

  @Transactional
  public void handleSubscriptionCreated(Subscription subscription) {
    ActivityDocument activity = activityRepository.findByUserId(subscription.getUser().getId())
        .orElseGet(() -> createNewActivityDocument(subscription.getUser().getId()));

    SubscriptionData subscriptionData = SubscriptionData.builder()
        .subscriptionId(subscription.getId())
        .interestId(subscription.getInterest().getId())
        .interestName(subscription.getInterest() != null ? subscription.getInterest().getName() : null)
        .createdAt(subscription.getCreatedAt())
        .build();

    activity.getSubscriptions().add(subscriptionData);
    activityRepository.save(activity);
  }

  // 새 ActivityDocument 생성
  private ActivityDocument createNewActivityDocument(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    return ActivityDocument.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .createdAt(Instant.now())
        .subscriptions(new ArrayList<>())
        .comments(new ArrayList<>())
        .commentLikes(new ArrayList<>())
        .articleViews(new ArrayList<>())
        .build();
  }
}