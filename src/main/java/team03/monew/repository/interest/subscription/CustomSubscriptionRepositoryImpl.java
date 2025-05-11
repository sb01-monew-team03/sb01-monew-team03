package team03.monew.repository.interest.subscription;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team03.monew.entity.interest.QInterest;
import team03.monew.entity.interest.QSubscription;
import team03.monew.entity.user.QUser;

@Repository
@RequiredArgsConstructor
public class CustomSubscriptionRepositoryImpl implements CustomSubscriptionRepository {

  private final JPAQueryFactory queryFactory;
  private final QSubscription qSubscription = QSubscription.subscription;
  private final QInterest qInterest = QInterest.interest;
  private final QUser qUser = QUser.user;

  @Override
  public List<UUID> findInterestIdsByUserId(UUID userId) {
    return queryFactory
        .select(qSubscription.interest.id)
        .from(qSubscription)
        .where(qSubscription.user.id.eq(userId))
        .fetch();
  }
}
