package team03.monew.entity.article;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import team03.monew.entity.base.BaseEntity;
import team03.monew.entity.interest.Interest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "articles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE articles SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Article extends BaseEntity {

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

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int viewCount = 0;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

    // 연관 관심사 (ManyToMany)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "article_interest",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    private Set<Interest> interests = new HashSet<>();

    public Article(String source, String originalLink, String title, String summary, LocalDateTime publishedAt) {
        this.source = source;
        this.originalLink = originalLink;
        this.title = title;
        this.summary = summary;
        this.publishedAt = publishedAt;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void delete() {
        this.deleted = true;
    }
}
