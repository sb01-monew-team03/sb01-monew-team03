package team03.monew.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseUpdatableEntity extends BaseEntity {

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;
}
