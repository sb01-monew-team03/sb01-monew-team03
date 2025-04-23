package team03.monew.service.interest;

import java.util.UUID;
import team03.monew.dto.interest.SubscriptionDto;

public interface SubscriptionService {

  // 구독
  SubscriptionDto create(UUID userId, UUID interestId);
}
