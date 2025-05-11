package team03.monew.entity.article;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team03.monew.entity.base.BaseDeletableEntity;
import java.time.LocalDateTime;

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

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int viewCount = 0;

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

}
