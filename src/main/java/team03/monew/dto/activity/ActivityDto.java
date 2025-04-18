package team03.monew.dto.activity;

import java.util.List;
import team03.monew.entity.article.Article;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.comments.CommentLike;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.user.User;

public record ActivityDto(User user,
                          List<Interest> interestList,
                          List<Comment> commentList,
                          List<CommentLike> commentLikeList,
                          List<Article> articleList) {

}
