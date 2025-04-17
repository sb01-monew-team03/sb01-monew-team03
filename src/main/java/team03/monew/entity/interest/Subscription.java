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
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Entity
@NoArgsConstructor
@EntityListeners(EnableJpaAuditing.class)
@Table(name = "subscriptions")
@IdClass(SubscriptionId.class)
public class Subscription {

  // TODO: User 엔티티 추가 후 작성
//  @Id
//  @ManyToOne
//  @JoinColumn(name = "user_id", nullable = false)
//  private User user;

  @Id
  @ManyToOne
  @JoinColumn(name = "interest_id", nullable = false)
  private Interest interest;

  @CreatedDate
  @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
  private Instant createdAt;
}
