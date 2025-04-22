package team03.monew.repository.interest;

import java.util.List;
import team03.monew.dto.interest.InterestFindRequest;
import team03.monew.entity.interest.Interest;

public interface CustomInterestRepository {

  // 조건에 맞는 관심사 검색
  List<Interest> findInterest(InterestFindRequest request);
}
