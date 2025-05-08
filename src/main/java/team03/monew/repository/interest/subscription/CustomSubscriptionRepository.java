package team03.monew.repository.interest.subscription;

import java.util.List;
import java.util.UUID;
import team03.monew.entity.interest.Interest;

public interface CustomSubscriptionRepository {

  // 해당 유저가 구독한 관심사 아이디 리스트 반환
  List<UUID> findInterestIdsByUserId(UUID userId);

  // 해당 유저가 구독한 관심사 리스트 반환
  List<Interest> findInterestsByUserId(UUID userId);
}
