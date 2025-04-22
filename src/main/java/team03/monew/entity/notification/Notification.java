package team03.monew.entity.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import team03.monew.dto.notification.ResourceType;
import team03.monew.entity.base.BaseEntity;
import team03.monew.entity.user.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification")
public class Notification extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private Boolean confirmed;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ResourceType type;

  @Column(nullable = false)
  private UUID resourceId;

  public Notification(User user, String content, ResourceType type, UUID resourceId) {
    this.user = user;
    this.content = content;
    this.type = type;
    this.resourceId = resourceId;
    this.confirmed = false;
  }

  public void confirmed() {
    this.confirmed = true;
  }

}
