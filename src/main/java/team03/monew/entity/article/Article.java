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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "article_interest",
        joinColumns = @JoinColumn(name = "article_id"),
        inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    private Set<Interest> interests = new HashSet<>();

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

    public void updateInterests(Set<Interest> newInterests) {
        this.interests.clear();
        this.interests.addAll(newInterests);
    }

}
