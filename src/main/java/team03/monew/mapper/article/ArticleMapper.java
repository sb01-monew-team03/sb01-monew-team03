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

    @Mapping(target = "interestNames", source = "interests", qualifiedByName = "interestsToNames")
    ArticleDto toDto(Article article);

    @Named("interestsToNames")
    default Set<String> interestsToNames(Set<Interest> interests) {
        return interests.stream()
            .map(Interest::getName)
            .collect(Collectors.toSet());
    }

}
