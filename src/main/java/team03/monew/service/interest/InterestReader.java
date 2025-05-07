package team03.monew.service.interest;

import java.util.UUID;
import team03.monew.entity.interest.Interest;

public interface InterestReader {

  // 관심사 엔티티 반환
  Interest getInterestEntityById(UUID interestId);
}
