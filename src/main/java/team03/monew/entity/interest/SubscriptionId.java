package team03.monew.entity.interest;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team03.monew.entity.user.User;

// 복합 키용 클래스
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Getter
public class SubscriptionId implements Serializable {

  private UUID userId;
  private UUID interestId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubscriptionId that = (SubscriptionId) o;
    return Objects.equals(userId, that.userId) && Objects.equals(interestId,
        that.interestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, interestId);
  }
}
