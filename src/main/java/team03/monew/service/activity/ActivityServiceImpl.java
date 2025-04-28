package team03.monew.service.activity;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.dto.activity.ActivityDto;
import team03.monew.dto.article.ArticleViewDto;
import team03.monew.dto.comments.CommentDto;
import team03.monew.dto.comments.CommentLikeDtoForActivity;
import team03.monew.dto.interest.SubscriptionDto;
import team03.monew.entity.article.ArticleView;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.comments.CommentLike;
import team03.monew.entity.interest.Subscription;
import team03.monew.entity.user.User;
import team03.monew.mapper.ArticleViewMapper;
import team03.monew.mapper.comments.CommentLikeMapper;
import team03.monew.mapper.comments.CommentMapper;
import team03.monew.mapper.interest.SubscriptionMapper;
import team03.monew.repository.article.ArticleViewRepository;
import team03.monew.repository.comments.CommentLikeRepository;
import team03.monew.repository.comments.CommentRepository;
import team03.monew.repository.interest.SubscriptionRepository;
import team03.monew.repository.user.UserRepository;
import team03.monew.util.exception.user.UserNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ActivityServiceImpl implements ActivityService {

  private final UserRepository userRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final CommentRepository commentRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final ArticleViewRepository articleViewRepository;

  private final SubscriptionMapper subscriptionMapper;
  private final CommentMapper commentMapper;
  private final CommentLikeMapper commentLikeMapper;
  private final ArticleViewMapper articleViewMapper;

  @Override
  public ActivityDto findUserActivity(UUID userId) {
    if (!userRepository.existsById(userId)) {
      log.error("존재하지 않는 사용자 ID");
      throw UserNotFoundException.withId(userId);
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    log.debug("사용자 활동 내역 조회 시작: 사용자 ID = {}", userId);
    List<Subscription> subscriptions = subscriptionRepository.findAllByUser(user);
    List<SubscriptionDto> subscriptionDtos = subscriptions.stream()
        .map(subscriptionMapper::toDto).toList();

    List<Comment> comments = commentRepository.findTop10ByUserOrderByCreatedAtDesc(user);
    List<CommentDto> commentDtos = comments.stream()
        .map(commentMapper::toDto).toList();

    List<CommentLike> commentLikes = commentLikeRepository.findTop10ByUserOrderByCreatedAtDesc(user);
    List<CommentLikeDtoForActivity> commentLikeDtos = commentLikes.stream()
        .map(commentLikeMapper::toDto).toList();

    List<ArticleView> articleViews = articleViewRepository.findTop10ByUserOrderByViewedAtDesc(user);
    List<ArticleViewDto> articleViewDtos = articleViews.stream()
        .map(articleViewMapper::toDto).toList();

    ActivityDto activityDto = new ActivityDto(
        userId,
        user.getEmail(),
        user.getNickname(),
        user.getCreatedAt(),
        subscriptionDtos,
        commentDtos,
        commentLikeDtos,
        articleViewDtos);

    log.info("사용자 활동 내역 조회 완료: 사용자 ID = {}", userId);
    return activityDto;
  }
}
