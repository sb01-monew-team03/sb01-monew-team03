package team03.monew.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseDeletableEntity extends BaseUpdatableEntity{

  @Column(name = "deleted_at")
  private Instant deletedAt;

  public boolean isDeleted() {
    return deletedAt != null;
  }

  public void delete() {
    deletedAt = Instant.now();
  }
}