package team03.monew.dto.activity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import team03.monew.dto.article.ArticleViewDto;
import team03.monew.dto.comments.CommentDto;
import team03.monew.dto.comments.CommentLikeDtoForActivity;
import team03.monew.dto.interest.SubscriptionDto;

public record ActivityDto(
    UUID id,
    String email,
    String nickname,
    Instant createdAt,
    List<SubscriptionDto> subscriptions,
    List<CommentDto> comments,
    List<CommentLikeDtoForActivity> commentLikes,
    List<ArticleViewDto> articleViews) {

}
