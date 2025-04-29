package team03.monew.dto.user;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import team03.monew.dto.article.ArticleViewDto;
import team03.monew.dto.comments.CommentActivityDto;
import team03.monew.dto.comments.CommentLikeActivityDto;
import team03.monew.dto.interest.SubscriptionDto;

public record UserActivityDto(
    UUID id,
    String email,
    String nickname,
    Instant createdAt,
    List<SubscriptionDto> subscriptions,
    List<CommentActivityDto> comments,
    List<CommentLikeActivityDto> commentLikes,
    List<ArticleViewDto> articleViews) {

}
