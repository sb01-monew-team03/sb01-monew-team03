package team03.monew.repository.interest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Subscription;
import team03.monew.entity.user.User;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {


  boolean existsByUser_IdAndInterest_Id(UUID userId, UUID interestId);

  Optional<Subscription> findByUser_IdAndInterest_Id(UUID userId, UUID interestId);

  List<Subscription> findAllByInterest(Interest interest);

  List<Subscription> findAllByUser(User user);
}
