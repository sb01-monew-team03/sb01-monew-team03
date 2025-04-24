package team03.monew.repository.notification;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team03.monew.entity.notification.QNotification;

@Repository
@RequiredArgsConstructor
public class CumtomNotificationRepositoryImpl implements CustomNotificationRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public void confirmAllByUserId(UUID userId) {
    queryFactory
        .update(QNotification.notification)
        .where(QNotification.notification.user.id.eq(userId))
        .set(QNotification.notification.confirmed, true)
        .execute();
  }

  @Override
  public void deleteAllConfirmNotification(Instant time) {
    queryFactory
        .delete(QNotification.notification)
        .where(QNotification.notification.updatedAt.before(time)
        .and(QNotification.notification.confirmed.eq(true)))
        .execute();
  }
}