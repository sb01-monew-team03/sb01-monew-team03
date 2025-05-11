package team03.monew.mapper.activity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import team03.monew.document.ActivityDocument;
import team03.monew.document.ActivityDocument.ArticleViewData;
import team03.monew.document.ActivityDocument.CommentData;
import team03.monew.document.ActivityDocument.CommentLikeData;
import team03.monew.document.ActivityDocument.SubscriptionData;
import team03.monew.dto.article.ArticleViewDto;
import team03.monew.dto.comments.CommentActivityDto;
import team03.monew.dto.comments.CommentDto;
import team03.monew.dto.comments.CommentLikeActivityDto;
import team03.monew.dto.comments.CommentLikeDto;
import team03.monew.dto.interest.SubscriptionDto;
import team03.monew.dto.user.ActivityDto;
import team03.monew.entity.article.Article;
import team03.monew.entity.article.ArticleView;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.comments.CommentLike;
import team03.monew.entity.interest.Subscription;

@Component
public class ActivityMapper {

    // Document -> DTO 매핑
    public ActivityDto toDto(ActivityDocument document) {
        if (document == null) {
            return null;
        }

        List<SubscriptionDto> subscriptions = document.getSubscriptions().stream()
            .map(this::toSubscriptionDto)
            .collect(Collectors.toList());

        List<CommentActivityDto> comments = document.getComments().stream()
            .map(this::toCommentActivityDto)
            .collect(Collectors.toList());

        List<CommentLikeActivityDto> commentLikes = document.getCommentLikes().stream()
            .map(this::toCommentLikeDto)
            .collect(Collectors.toList());

        List<ArticleViewDto> articleViews = document.getArticleViews().stream()
            .map(this::toArticleViewDto)
            .collect(Collectors.toList());

        return ActivityDto.builder()
            .id(document.getUserId())
            .email(document.getEmail())
            .nickname(document.getNickname())
            .createdAt(document.getCreatedAt())
            .subscriptions(subscriptions)
            .comments(comments)
            .commentLikes(commentLikes)
            .articleViews(articleViews)
            .build();
    }

    // 내부 매핑 메소드들 - Document -> DTO
    private SubscriptionDto toSubscriptionDto(SubscriptionData data) {
        return SubscriptionDto.builder()
            .id(data.getSubscriptionId())
            .interestId(data.getInterestId())
            .interestName(data.getInterestName())
            .createdAt(data.getCreatedAt())
            .build();
    }

    private CommentActivityDto toCommentActivityDto(CommentData data) {
        return CommentActivityDto.builder()
            .id(data.getCommentId())
            .articleId(data.getArticleId())
            .articleTitle(data.getArticleTitle())
            .userId(data.getUserId())
            .userNickname(data.getUserNickname())
            .content(data.getContent())
            .likeCount(data.getLikeCount())
            .createdAt(data.getCreatedAt())
            .build();
    }

    private CommentLikeActivityDto toCommentLikeDto(CommentLikeData data) {
        return CommentLikeActivityDto.builder()
            .id(data.getId())
            .createdAt(data.getCreatedAt())
            .commentId(data.getCommentId())
            .articleId(data.getArticleId())
            .articleTitle(data.getArticleTitle())
            .commentUserId(data.getCommentUserId())
            .commentUserNickname(data.getCommentUserNickname())
            .commentContent(data.getCommentContent())
            .commentLikeCount(data.getCommentLikeCount())
            .createdAt(data.getCreatedAt())
            .build();
    }

    private ArticleViewDto toArticleViewDto(ArticleViewData data) {
        return ArticleViewDto.builder()
            .id(data.getId())
            .viewedBy(data.getViewedBy())
            .createdAt(data.getCreatedAt())
            .articleId(data.getArticleId())
            .source(data.getSource())
            .sourceUrl(data.getSourceUrl())
            .articleTitle(data.getArticleTitle())
            .articlePublishedDate(data.getArticlePublishedDate())
            .articleSummary(data.getArticleSummary())
            .articleCommentCount(data.getArticleCommentCount())
            .articleViewCount(data.getArticleViewCount())
            .build();
    }
//
//    // 내부 매핑 메소드들 - Entity -> Document Data
//    private SubscriptionData toSubscriptionData(Subscription subscription) {
//        return SubscriptionData.builder()
//            .subscriptionId(subscription.getId())
//            .interestId(subscription.getInterest().getId())
//            .interestName(subscription.getInterest() != null ? subscription.getInterest().getName() : null)
//            .createdAt(subscription.getCreatedAt())
//            .build();
//    }
//
//    private CommentData toCommentData(Comment comment) {
//        return CommentData.builder()
//            .commentId(comment.getId())
//            .content(comment.getContent())
//            .userId(comment.getUser().getId())
//            .userNickname(comment.getUser() != null ? comment.getUser().getNickname() : null)
//            .articleId(comment.getArticle().getId())
//            .articleTitle(comment.getArticle() != null ? comment.getArticle().getTitle() : null)
//            .createdAt(comment.getCreatedAt())
//            .likeCount(comment.getLikeCount())
//            .build();
//    }
//
//    private CommentLikeData toCommentLikeData(CommentLike commentLike) {
//        return CommentLikeData.builder()
//            .id(commentLike.getId())
//            .commentId(commentLike.getComment().getId())
//            .comm(commentLike.getUser().getId())
//            .articleId(commentLike.getArticle().getId())
//            .articleTitle(commentLike.getArticle() != null ? commentLike.getArticle().getTitle() : null)
//            .createdAt(commentLike.getCreatedAt())
//            .build();
//    }
//
//    private ArticleViewData toArticleViewData(ArticleView articleView) {
//        return ArticleViewData.builder()
//            .id(articleView.getId())
//            .viewedBy(articleView.getUser().getId())
//            .articleId(articleView.getArticle().getId())
//            .articleTitle(articleView.getArticle() != null ? articleView.getArticle().getTitle() : null)
//            .createdAt(articleView.getCreatedAt())
//            .build();
//    }
}