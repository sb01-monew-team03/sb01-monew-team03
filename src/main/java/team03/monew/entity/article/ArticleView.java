package team03.monew.entity.article;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team03.monew.entity.base.BaseUpdatableEntity;
import team03.monew.entity.user.User;

@Entity
@Getter
@Table(name = "article_view")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleView extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    private Instant viewedAt;

    public ArticleView(User user, Article article) {
        this.user = user;
        this.article = article;
        this.viewedAt = Instant.now();
    }

}