package team03.monew.repository.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team03.monew.entity.user.User;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.id = :id AND u.deletedAt IS NULL")
  Optional<User> findActiveById(UUID id);
}
