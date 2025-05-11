package team03.monew.mapper.article;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import team03.monew.dto.article.ArticleDto;
import team03.monew.entity.article.Article;
import team03.monew.entity.interest.Interest;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "commentCount", source = "commentCount")
    @Mapping(target = "viewedByMe", source = "viewedByMe")
    ArticleDto toDto(Article article, int commentCount, boolean viewedByMe);
}