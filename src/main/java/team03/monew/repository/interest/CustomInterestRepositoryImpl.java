package team03.monew.repository.interest;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team03.monew.dto.interest.InterestFindRequest;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.QInterest;
import team03.monew.entity.interest.QKeyword;

@Repository
@RequiredArgsConstructor
public class CustomInterestRepositoryImpl implements CustomInterestRepository {

  private final JPAQueryFactory queryFactory;
  private final QInterest qInterest = QInterest.interest;
  private final QKeyword qKeyword = QKeyword.keyword;

  @Override
  public List<Interest> findInterest(InterestFindRequest request) {

    BooleanExpression where = null;

    if (!StringUtils.isNullOrEmpty(request.keyword())) {
      where = containsInterestName(request.keyword())
          .or(containsKeywordsName(request.keyword()));
    }

    return queryFactory
        .select(qInterest)
        .from(qInterest)
        .join(qInterest.keywords, qKeyword).fetchJoin()
        .where(
            where,
            cursorCondition(request.cursor(), request.orderBy(), request.direction(),
                request.after())
        )
        .orderBy(
            getOrderBy(request.orderBy(), request.direction()),
            qInterest.createdAt.asc())
        .limit(request.limit())
        .fetch();
  }


  // where
  // 관심사 이름
  private BooleanExpression containsInterestName(String keyword) {

    return qInterest.name.contains(keyword);
  }

  // 키워드
  private BooleanExpression containsKeywordsName(String keyword) {

    return qKeyword.name.contains(keyword);
  }

  // orderBy
  // 관심사 이름 or 구독자 수
  private OrderSpecifier<?> getOrderBy(String orderBy, String direction) {

    boolean isDesc = "desc".equalsIgnoreCase(direction);

    switch (orderBy) {
      case "name":
        return isDesc ? qInterest.name.desc() : qInterest.name.asc();
      case "subscriberCount":
        return isDesc ? qInterest.subscriberCount.desc() : qInterest.subscriberCount.asc();
    }

    return null;
  }

  // 커서 페이지네이션
  // 커서
  private BooleanExpression cursorCondition(String cursor, String orderBy, String direction,
      String after) {

    boolean isDesc = "desc".equalsIgnoreCase(direction);

    if (!StringUtils.isNullOrEmpty(cursor)) {
      switch (orderBy) {
        // 내림차순일 경우 cursor의 값이 작거나, 같되 after(create_at)이 커야 함
        // 오름차순일 경우 cursor의 값이 크거나, 같되 after(create_at)이 커야 함
        case "name":
          return isDesc
              ? qInterest.name.lt(cursor)
              .or(qInterest.name.eq(cursor).and(afterCondition(after)))
              : qInterest.name.gt(cursor)
                  .or(qInterest.name.eq(cursor).and(afterCondition(after)));
        case "subscriberCount":
          return isDesc
              ? qInterest.subscriberCount.lt(Long.parseLong(cursor))
              .or(qInterest.subscriberCount.eq(Long.parseLong(cursor))
                  .and(afterCondition(after)))
              : qInterest.subscriberCount.gt(Long.parseLong(cursor))
                  .or(qInterest.subscriberCount.eq(Long.parseLong(cursor))
                      .and(afterCondition(after)));
      }
    }

    return afterCondition(after);
  }

  // after
  private BooleanExpression afterCondition(String after) {
    if (StringUtils.isNullOrEmpty(after)) {
      return null;
    }

    return qInterest.createdAt.gt(Instant.parse(after));
  }
}
