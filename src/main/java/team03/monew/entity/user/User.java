package team03.monew.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false, nullable = false)
  private UUID id;

  @Column(length = 100, nullable = false)
  private String nickname;

  @Column(length = 50, updatable = false, nullable = false, unique = true)
  private String email;

  @Column(length = 100, updatable = false, nullable = false)
  private String password;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;

  private Instant deletedAt;

  @Enumerated(EnumType.STRING)
  private Role role;

  public boolean isDeleted() {
    return deletedAt != null;
  }

  public User(String nickname, String email, String password) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;
  }

  public void update(String nickname) {
    this.nickname = nickname;
  }

  public void delete() {
    deletedAt = Instant.now();
  }

  public enum Role {
    USER, ADMIN
  }
}

