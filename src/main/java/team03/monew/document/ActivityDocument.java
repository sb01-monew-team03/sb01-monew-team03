package team03.monew.document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import team03.monew.entity.article.Article;
import team03.monew.entity.article.ArticleView;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.comments.CommentLike;
import team03.monew.entity.interest.Subscription;
import team03.monew.entity.user.User;

@Document(collection = "activity")
@CompoundIndex(name = "user_id_idx", def = "{'userId': 1}")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class ActivityDocument {

  private UUID userId;
  private String email;
  private String nickname;
  private Instant createdAt;

  @Builder.Default
  private List<SubscriptionData> subscriptions = new ArrayList<>();

  @Builder.Default
  private List<CommentData> comments = new ArrayList<>();

  @Builder.Default
  private List<CommentLikeData> commentLikes = new ArrayList<>();

  @Builder.Default
  private List<ArticleViewData> articleViews = new ArrayList<>();

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SubscriptionData {
    private UUID subscriptionId;
    private UUID interestId;
    private String interestName;
    private Instant createdAt;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CommentData {
    private UUID commentId;
    private String content;
    private UUID userId;
    private String userNickname;
    private UUID articleId;
    private String articleTitle;
    private Instant createdAt;
    private Integer likeCount;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CommentLikeData {
    private UUID id;
    private Instant createdAt;
    private UUID commentId;
    private UUID articleId;
    private String articleTitle;
    private UUID commentUserId;
    private String commentUserNickname;
    private String commentContent;
    private int commentLikeCount;
    private Instant commentCreatedAt;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ArticleViewData {
    private UUID id;
    private UUID viewedBy;
    private Instant createdAt;
    private UUID articleId;
    private String source;
    private String sourceUrl;
    private String articleTitle;
    private Instant articlePublishedDate;
    private String articleSummary;
    private Long articleCommentCount;
    private Long articleViewCount;
  }

}
