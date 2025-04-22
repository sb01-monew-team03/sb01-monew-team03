package team03.monew.repository.interest;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
        .select(qInterest)
        .from(qInterest)
        .where()
        .orderBy()
        .limit(request.limit())
        .fetch();
  }


  // where
  // 관심사 이름
  private BooleanExpression containsInterestName(String keyword) {
    if (StringUtils.isNullOrEmpty(keyword)) {
      return null;
    }
    return qInterest.name.contains(keyword);
  }
}
