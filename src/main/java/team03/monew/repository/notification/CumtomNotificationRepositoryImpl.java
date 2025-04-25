package team03.monew.repository.notification;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team03.monew.entity.notification.Notification;
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

  @Override
  public Page<Notification> findPageWithCursor(UUID userId, String cursor, Pageable pageable) {
    BooleanExpression cursorCondition = null;
    if (cursor != null && !cursor.isEmpty()) {
      try {
        Instant cursorDateTime = Instant.parse(cursor);
        cursorCondition = QNotification.notification.createdAt.lt(cursorDateTime);
      } catch (Exception e) {
        throw new IllegalArgumentException("Invalid cursor format", e);
      }
    }

    List<Notification> content = queryFactory
        .selectFrom(QNotification.notification)
        .where(QNotification.notification.user.id.eq(userId)
            .and(QNotification.notification.confirmed.eq(false)))
        .orderBy(QNotification.notification.createdAt.desc())
        .limit(pageable.getPageSize() + 1)
        .fetch();

    boolean hasNext = content.size() > pageable.getPageSize();

    if (hasNext) {
      content = content.subList(0, pageable.getPageSize());
    }

    long total;
    if (cursor == null || cursor.isEmpty()) {
      Long count = queryFactory
          .select(QNotification.notification.count())
          .from(QNotification.notification)
          .where(QNotification.notification.user.id.eq(userId)
              .and(QNotification.notification.confirmed.eq(false)))
          .fetchOne();
      total = count != null ? count : 0L;
    } else {
      total = hasNext ? Long.MAX_VALUE : content.size();
    }

    return new PageImpl<>(content, pageable, total);
  }

}