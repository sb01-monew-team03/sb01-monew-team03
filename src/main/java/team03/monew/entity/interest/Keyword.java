package team03.monew.entity.interest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import team03.monew.entity.base.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "keywords")
public class Keyword extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "interest_id", nullable = false)
  private Interest interest;

  @Column(nullable = false, length = 30)
  private String name;

  public Keyword(Interest interest, String name) {
    this.interest = interest;
    this.name = name;
  }
}
