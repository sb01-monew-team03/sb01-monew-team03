package team03.monew.entity.interest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team03.monew.entity.base.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "interests")
@Getter
public class Interest extends BaseEntity {

  @Column(unique = true, nullable = false, length = 60)
  // JPA와 postgreSQL에서 길이는 글자 수(한글, 영어 관계없음)
  private String name;  // 관심사명

  @Column(nullable = false)
  private long subscriberCount; // 구독자 수

  // 부모 객체 삭제 시 자식 객체도 삭제, 고아 객체 삭제
  @OneToMany(mappedBy = "interest", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
  private List<Keyword> keywords;   // 키워드

  public Interest(String name) {
    this.name = name;
    this.subscriberCount = 0;
    this.keywords = new ArrayList<>();
  }

  // 구독자 수 증가
  public void increaseSubscribers() {
    this.subscriberCount++;
  }

  // 구독자 수 감소
  public void decreaseSubscribers() {
    if (this.subscriberCount > 0) {
      this.subscriberCount--;
    }
  }

  // 키워드 수정
  public void updateKeywords(List<String> keywords) {
    this.keywords.clear();    // keywords 내부 비움

    Set<String> keywordsSet = new LinkedHashSet<>(keywords);    // 중복 제거, 사용자 요청 순서 보장

    for (String keywordName : keywordsSet) {    // keywords 내부 다시 채움
      Keyword keyword = new Keyword(this, keywordName);
      this.keywords.add(keyword);
    }
  }

  // name 같은 경우 동일 객체로 취급
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Interest interest = (Interest) o;
    return Objects.equals(name, interest.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }
}