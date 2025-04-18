package team03.monew.entity.interest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team03.monew.entity.base.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "keywords")
@Getter
public class Keyword extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "interest_id", nullable = false)
  private Interest interest;  // 해당 키워드가 속하는 관심사

  @Column(nullable = false, length = 30)
  private String name;  // 키워드 이름

  public Keyword(Interest interest, String name) {
    this.name = name;
  }

  // interest, name 동일한 경우 같은 객체로 취급
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Keyword keyword = (Keyword) o;
    return Objects.equals(interest, keyword.interest) && Objects.equals(name,
        keyword.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(interest, name);
  }
}
