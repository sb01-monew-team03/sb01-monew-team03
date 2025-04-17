package team03.monew.entity.interest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "interests")
public class Interest {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(nullable = false)
  private long subscriberCount;

  @CreatedDate
  @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
  private Instant createdAt;

  public Interest(String name) {
    this.name = name;
    this.subscriberCount = 0;
  }

  public void increaseSubscribers() {
    this.subscriberCount++;
  }

  public void decreaseSubscribers() {
    if (this.subscriberCount > 0) {
      this.subscriberCount--;
    }
  }
}