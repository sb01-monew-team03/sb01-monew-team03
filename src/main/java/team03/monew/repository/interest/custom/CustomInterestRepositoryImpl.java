package team03.monew.repository.interest.custom;

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

    return queryFactory
        .select(qInterest).distinct() // 중복 제거(키워드 수만큼 row 반복 막음)
        .from(qInterest)
        .join(qInterest.keywords, qKeyword).fetchJoin()
        .where(
            getWhere(request.keyword()),
            cursorCondition(request.cursor(), request.orderBy(), request.direction(),
                request.after())
        )
        .orderBy(
            getOrderBy(request.orderBy(), request.direction()),
            qInterest.createdAt.asc())
        .limit(request.limit() + 1) // 다음 페이지 확인용으로 1개 더 읽어옴
        .fetch();
  }

  @Override
  public long totalCountInterest(InterestFindRequest request) {

    Long count = queryFactory
        .select(qInterest.countDistinct())  // 중복 제거를 통해 순수 관심사 개수 반환
        .from(qInterest)
        .join(qInterest.keywords, qKeyword)
        .where(getWhere(request.keyword()))
        .fetchOne();

    return count == null ? 0 : count;
  }

  // where
  // 조건절 취합
  private BooleanExpression getWhere(String keyword) {
    BooleanExpression where = null;

    if (!StringUtils.isNullOrEmpty(keyword)) {
      where = containsInterestName(keyword)
          .or(containsKeywordsName(keyword));
    }

    return where;
  }

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
