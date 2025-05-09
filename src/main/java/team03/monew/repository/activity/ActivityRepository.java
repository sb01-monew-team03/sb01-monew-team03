package team03.monew.repository.activity;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import team03.monew.document.ActivityDocument;

@Repository
public interface ActivityRepository extends MongoRepository<ActivityDocument, UUID> {
  Optional<ActivityDocument> findByUserId(UUID userId);

}
