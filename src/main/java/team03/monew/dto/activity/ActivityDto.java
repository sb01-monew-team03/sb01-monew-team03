package team03.monew.dto.activity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import team03.monew.entity.article.Article;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.comments.CommentLike;
import team03.monew.entity.interest.Interest;

public record ActivityDto(
    UUID id,
    String email,
    String nickname,
    LocalDateTime createdAt,
    List<Interest> subscriptions,
    List<Comment> comments,
    List<CommentLike> commentLikes,
    List<Article> articleViews) {

}
