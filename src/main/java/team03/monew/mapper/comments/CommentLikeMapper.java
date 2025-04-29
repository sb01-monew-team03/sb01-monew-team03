package team03.monew.mapper.comments;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import team03.monew.dto.comments.CommentLikeActivityDto;
import team03.monew.entity.comments.CommentLike;

@Mapper(componentModel = "spring")
public interface CommentLikeMapper {

  @Mapping(source = "comment.id", target = "commentId")
  @Mapping(source = "comment.article.id", target = "articleId")
  @Mapping(source = "comment.article.title", target = "articleTitle")
  @Mapping(source = "comment.user.id", target = "commentUserId")
  @Mapping(source = "comment.user.nickname", target = "commentUserNickname")
  @Mapping(source = "comment.content", target = "commentContent")
  @Mapping(source = "comment.likeCount", target = "commentLikeCount")
  @Mapping(source = "comment.createdAt", target = "commentCreatedAt")
  CommentLikeActivityDto toDto(CommentLike commentLike);
}
