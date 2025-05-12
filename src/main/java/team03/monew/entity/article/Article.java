package team03.monew.entity.article;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team03.monew.entity.base.BaseDeletableEntity;
import java.time.LocalDateTime;
import team03.monew.entity.interest.Interest;

@Entity
@Table(name = "articles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseDeletableEntity {

    @Column(nullable = false)
    private String source;

    @Column(nullable = false, unique = true)
    private String originalLink;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private long viewCount = 0;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArticleInterest> interests = new HashSet<>();

    public Article(String source, String originalLink, String title, String summary,
        LocalDateTime publishedAt) {
        this.source = source;
        this.originalLink = originalLink;
        this.title = title;
        this.summary = summary;
        this.publishedAt = publishedAt;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void addInterest(Interest interest) {
        // 이미 관심사가 있는지 확인
        boolean exists = this.interests.stream()
            .anyMatch(ai -> ai.getInterest().getId().equals(interest.getId()));

        if (!exists) {
            ArticleInterest articleInterest = new ArticleInterest(this, interest);
            this.interests.add(articleInterest);
        }
    }

    public void removeInterest(Interest interest) {
        this.interests.removeIf(ai -> ai.getInterest().getId().equals(interest.getId()));
    }

    public void updateInterests(Set<Interest> newInterests) {
        // 기존 관심사 모두 제거
        this.interests.clear();

        // 새 관심사 추가
        if (newInterests != null) {
            for (Interest interest : newInterests) {
                addInterest(interest);
            }
        }
    }
}
