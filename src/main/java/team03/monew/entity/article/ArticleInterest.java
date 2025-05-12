package team03.monew.entity.article;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team03.monew.entity.base.BaseUpdatableEntity;
import team03.monew.entity.interest.Interest;

@Entity
@Table(name = "article_interest", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"article_id", "interest_id"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleInterest extends BaseUpdatableEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "article_id", nullable = false)
  private Article article;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interest_id", nullable = false)
  private Interest interest;

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof ArticleInterest)) {
      return false;
    }

    ArticleInterest that = (ArticleInterest) object;

    return Objects.equals(article != null ? article.getId() : null,
        that.article != null ? that.article.getId() : null)
        && Objects.equals(interest != null ? interest.getId() : null,
        that.interest != null ? that.interest.getId() : null);
  }
}
