package team03.monew.mapper.comments;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import team03.monew.dto.comments.CommentActivityDto;
import team03.monew.dto.comments.CommentDto;
import team03.monew.dto.comments.CommentLikeDto;
import team03.monew.dto.comments.CommentActivityDto;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.comments.CommentLike;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "articleId", source = "comment.article.id")
    @Mapping(target = "userId", source = "comment.user.id")
    @Mapping(target = "userNickname", source = "comment.user.nickname")
    @Mapping(target = "content", source = "comment.content")
    @Mapping(target = "likeCount", source = "comment.likeCount")
    @Mapping(target = "likedByMe", source = "likedByMe")
    @Mapping(target = "createdAt", source = "comment.createdAt")
    CommentDto toDto(Comment comment, boolean likedByMe);

    @Mapping(target = "id", source = "like.id")
    @Mapping(target = "likedBy", source = "like.user.id")
    @Mapping(target = "createdAt", source = "like.createdAt")
    @Mapping(target = "commentId", source = "like.comment.id")
    @Mapping(target = "articleId", source = "like.article.id")
    @Mapping(target = "commentUserId", source = "like.comment.user.id")
    @Mapping(target = "commentUserNickname", source = "like.comment.user.nickname")
    @Mapping(target = "commentContent", source = "like.comment.content")
    @Mapping(target = "commentLikeCount", source = "like.comment.likeCount")
    @Mapping(target = "commentCreatedAt", source = "like.comment.createdAt")
    CommentLikeDto toLikeDto(CommentLike like);

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "articleId", source = "comment.article.id")
    @Mapping(target = "articleTitle", source = "comment.article.title")
    @Mapping(target = "userId", source = "comment.user.id")
    @Mapping(target = "userNickname", source = "comment.user.nickname")
    @Mapping(target = "content", source = "comment.content")
    @Mapping(target = "likeCount", source = "comment.likeCount")
    @Mapping(target = "createdAt", source = "comment.createdAt")
    CommentActivityDto toActivityDto(Comment comment);
}
