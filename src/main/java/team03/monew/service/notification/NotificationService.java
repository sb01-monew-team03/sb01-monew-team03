package team03.monew.service.notification;

import java.util.List;
import java.util.UUID;
import team03.monew.controller.notification.NotificationRequestForm;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.dto.notification.NotificationDto;
import team03.monew.entity.article.Article;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.user.User;

public interface NotificationService {
  List<NotificationDto> createInterestNotification(List<Article> articles);

  NotificationDto createCommentLikeNotification(Comment comment, User user);

  void readNotification(UUID id, UUID userId);

  void readAllNotification(UUID userId);

  CursorPageResponse<NotificationDto> findAll(NotificationRequestForm request);

}
