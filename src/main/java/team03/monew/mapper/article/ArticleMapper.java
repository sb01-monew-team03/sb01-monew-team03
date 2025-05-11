package team03.monew.mapper.article;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import team03.monew.dto.article.ArticleDto;
import team03.monew.dto.article.ArticleViewDto;
import team03.monew.entity.article.Article;
import team03.monew.entity.article.ArticleView;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "sourceUrl", source = "article.originalLink")
    @Mapping(target = "publishDate", source = "article.publishedAt")
    @Mapping(target = "commentCount", source = "commentCount")
    @Mapping(target = "viewedByMe", source = "viewedByMe")
    ArticleDto toDto(Article article, int commentCount, boolean viewedByMe);

    @Mapping(target = "id", source = "articleView.id")
    @Mapping(target = "viewedBy", source = "articleView.user.id")
    @Mapping(target = "createdAt", source = "articleView.createdAt")
    @Mapping(target = "articleId", source = "article.id")
    @Mapping(target = "source", source = "article.source")
    @Mapping(target = "sourceUrl", source = "article.originalLink")
    @Mapping(target = "articleTitle", source = "article.title")
    @Mapping(target = "articlePublishedDate", source = "article.publishedAt")
    @Mapping(target = "articleSummary", source = "article.summary")
    @Mapping(target = "articleViewCount", source = "article.viewCount")
    @Mapping(target = "articleCommentCount", source = "commentCount")
    ArticleViewDto toViewDto(ArticleView articleView, Article article, long commentCount);

    default Instant map(LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}