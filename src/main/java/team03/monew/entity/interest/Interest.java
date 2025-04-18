package team03.monew.entity.interest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import team03.monew.entity.base.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "interests")
public class Interest extends BaseEntity {

  @Column(nullable = false, length = 60)    // JPA와 postgreSQL에서 길이는 글자 수(한글, 영어 관계없음)
  private String name;

  @Column(nullable = false)
  private long subscriberCount;

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