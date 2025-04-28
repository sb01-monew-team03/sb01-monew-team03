package team03.monew.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import team03.monew.dto.article.ArticleViewDto;
import team03.monew.entity.article.ArticleView;
import team03.monew.repository.comments.CommentRepository;

@Mapper(componentModel = "spring")
public interface ArticleViewMapper {

  @Mapping(source = "articleView.id", target = "id")
  @Mapping(source = "articleView.user.id", target = "viewedBy")
  @Mapping(source = "articleView.createdAt", target = "createdAt")
  @Mapping(source = "articleView.article.id", target = "articleId")
  @Mapping(source = "articleView.article.source", target = "source")
  @Mapping(source = "articleView.article.originalLink", target = "sourceUrl")
  @Mapping(source = "articleView.article.title", target = "articleTitle")
  @Mapping(source = "articleView.article.createdAt", target = "articlePublishedDate")
  @Mapping(source = "articleView.article.summary", target = "articleSummary")
  @Mapping(expression = "java(commentRepository.countByArticle(articleView.getArticle()))",
      target = "articleCommentCount")
  @Mapping(source = "articleView.article.viewCount", target = "articleViewCount")
  ArticleViewDto toDto(ArticleView articleView, @Context CommentRepository commentRepository);
}
