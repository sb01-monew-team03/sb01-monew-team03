package team03.monew.mapper.interest;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import team03.monew.dto.interest.InterestDto;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Keyword;

@Mapper(componentModel = "spring")
public interface InterestMapper {

  @Mapping(target = "id", expression = "java(interest.getId().toString())")
  @Mapping(source = "interest.keywords", target = "keywords", qualifiedByName = "keywordsToNames")
  @Mapping(target = "subscribedByMe", source = "subscribedByMe")
  InterestDto toDto(Interest interest, boolean subscribedByMe);

  @Named("keywordsToNames")
  default List<String> keywordsToNames(List<Keyword> keywords) {
    return keywords.stream()
        .map(Keyword::getName)
        .collect(Collectors.toList());
  }
}
