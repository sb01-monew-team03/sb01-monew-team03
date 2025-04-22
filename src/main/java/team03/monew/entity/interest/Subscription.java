package team03.monew.entity.interest;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import team03.monew.entity.base.BaseEntity;
import team03.monew.entity.user.User;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(EnableJpaAuditing.class)
@Table(
    name = "subscriptions",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "interest_id"})
    }
)
@Getter
public class Subscription extends BaseEntity {


  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "interest_id", nullable = false)
  private Interest interest;
}
