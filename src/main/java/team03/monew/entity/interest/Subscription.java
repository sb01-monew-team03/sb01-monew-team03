package team03.monew.entity.interest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import team03.monew.entity.user.User;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(EnableJpaAuditing.class)
@Table(name = "subscriptions")
@IdClass(SubscriptionId.class)
@Getter
public class Subscription {   // 복합 키를 사용하기 때문에 BaseEntity 상속하지 않음

  @Id
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Id
  @ManyToOne
  @JoinColumn(name = "interest_id", nullable = false)
  private Interest interest;

  @CreatedDate
  @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
  private Instant createdAt;
}
