package team03.monew.repository.interest;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import team03.monew.entity.interest.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {


  boolean existsByUser_IdAndInterest_Id(UUID userId, UUID interestId);

  Optional<Subscription> findByUser_IdAndInterest_Id(UUID userId, UUID interestId);
}
