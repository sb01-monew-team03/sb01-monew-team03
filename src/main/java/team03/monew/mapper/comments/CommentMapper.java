package team03.monew.mapper.comments;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import team03.monew.dto.comments.CommentDto;
import team03.monew.entity.comments.Comment;

@Component
@Mapper(componentModel = "spring")
public interface CommentMapper {

    // Comment 엔티티를 CommentDto로 변환
    CommentDto toDto(Comment comment);

    // CommentDto를 Comment 엔티티로 변환
    Comment toEntity(CommentDto commentDto);
}