package team03.monew.entity.interest;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

// 복합 키용 클래스
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionId implements Serializable {

  private UUID userId;
  private UUID InterestId;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof SubscriptionId)) {
      return false;
    }
    SubscriptionId that = (SubscriptionId) obj;
    return Objects.equals(this.userId, that.userId)
        && Objects.equals(this.InterestId, that.InterestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, InterestId);
  }
}
