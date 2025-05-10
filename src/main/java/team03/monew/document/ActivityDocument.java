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
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import team03.monew.entity.article.ArticleView;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.comments.CommentLike;
import team03.monew.entity.interest.Subscription;

@Document(collection = "activity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class ActivityDocument {

  @Id
  private UUID userId;
  private String email;
  private String nickname;
  private Instant createdAt;

  @Builder.Default
  private List<Subscription> subscriptions = new ArrayList<>();

  @Builder.Default
  private List<Comment> comments = new ArrayList<>();

  @Builder.Default
  private List<CommentLike> commentLikes = new ArrayList<>();

  @Builder.Default
  private List<ArticleView> articleViews = new ArrayList<>();

}
