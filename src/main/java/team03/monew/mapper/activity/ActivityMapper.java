package team03.monew.mapper.activity;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import team03.monew.document.ActivityDocument;
import team03.monew.dto.article.ArticleViewDto;
import team03.monew.dto.comments.CommentActivityDto;
import team03.monew.dto.comments.CommentLikeActivityDto;
import team03.monew.dto.interest.SubscriptionDto;
import team03.monew.dto.user.ActivityDto;
import team03.monew.mapper.article.ArticleViewMapper;
import team03.monew.mapper.comments.CommentLikeMapper;
import team03.monew.mapper.comments.CommentMapper;
import team03.monew.mapper.interest.SubscriptionMapper;
import team03.monew.repository.comments.CommentRepository;

@Component
@AllArgsConstructor
public class ActivityMapper {

  private final SubscriptionMapper subscriptionMapper;
  private final CommentMapper commentMapper;
  private final CommentLikeMapper commentLikeMapper;
  private final ArticleViewMapper articleViewMapper;
  private final CommentRepository commentRepository;

  public ActivityDto toDto(ActivityDocument document) {
    if (document == null) {
      return null;
    }

    List<SubscriptionDto> subscriptionDtos = document.getSubscriptions()
        .stream()
        .map(subscriptionMapper::toDto)
        .toList();

    List<CommentActivityDto> commentDtos = document.getComments()
        .stream()
        .map(commentMapper::toActivityDto)
        .toList();

    List<CommentLikeActivityDto> commentLikeDtos = document.getCommentLikes()
        .stream()
        .map(commentLikeMapper::toActivityDto)
        .toList();

    List<ArticleViewDto> articleViewDtos = document.getArticleViews()
        .stream()
        .map(articleView -> articleViewMapper.toDto(articleView, commentRepository))
        .toList();

    return new ActivityDto(
        document.getUserId(),
        document.getEmail(),
        document.getNickname(),
        document.getCreatedAt(),
        subscriptionDtos,
        commentDtos,
        commentLikeDtos,
        articleViewDtos
    );
  }
}