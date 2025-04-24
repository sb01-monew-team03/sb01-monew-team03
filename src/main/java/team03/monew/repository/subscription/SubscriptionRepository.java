package team03.monew.repository.subscription;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

  List<Subscription> findAllByInterest(Interest interest);
}
