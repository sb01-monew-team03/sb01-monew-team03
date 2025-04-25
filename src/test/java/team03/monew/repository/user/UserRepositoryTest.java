package team03.monew.repository.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import team03.monew.entity.user.User;
import team03.monew.entity.user.User.Role;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EntityManager entityManager;

  @Nested
  @DisplayName("이메일로 사용자 여부 검색")
  class existsByEmail {

    @Test
    @DisplayName("이메일로 사용자를 찾았을 경우")
    void existsByEmail_success() {
      // given
      String email = "user@gmail.com";
      User user = new User("user", email, "qwer1234", Role.USER);
      userRepository.save(user);
      entityManager.flush();
      entityManager.clear();

      // when
      boolean exists = userRepository.existsByEmail(email);

      // then
      assertTrue(exists);
    }

    @Test
    @DisplayName("해당 이메일의 사용자가 없을 경우")
    void existsByEmail_returnFalse() {
      // given
      String email = "user@gmail.com";

      // when
      boolean exists = userRepository.existsByEmail(email);

      // then
      assertFalse(exists);
    }
  }

  @Nested
  @DisplayName("이메일로 사용자 찾기")
  class findByEmail {

    @Test
    @DisplayName("사용자 찾기 성공")
    void findByEmail_success() {
      // given
      String email = "user@gmail.com";
      User user = new User("user", email, "qwer1234", Role.USER);
      userRepository.save(user);
      entityManager.flush();
      entityManager.clear();

      // when
      Optional<User> result = userRepository.findByEmail(email);

      // then
      assertThat(result).isPresent();
      assertEquals(result.get().getNickname(), user.getNickname());
    }

    @Test
    @DisplayName("사용자 찾기 실패")
    void findByEmail_fail() {
      // given
      String email = "user@gmail.com";

      // when
      Optional<User> result = userRepository.findByEmail(email);

      // then
      assertThat(result).isEmpty();
    }
  }

  @Nested
  @DisplayName("활동중인 사용자 찾기")
  class findActiveById {

    @Test
    @DisplayName("활동중인 사용자 찾기 성공")
    void findActiveById_success() {
      // given
      User user = new User("test", "test@gmail.com", "qwer1234!", Role.USER);
      userRepository.save(user);
      entityManager.flush();
      entityManager.clear();
      UUID userId = user.getId();

      // when
      Optional<User> result = userRepository.findActiveById(userId);

      // then
      assertThat(result).isPresent();
      assertEquals(result.get().getNickname(), user.getNickname());
    }

    @Test
    @DisplayName("삭제됐을 경우")
    void findActiveById_alreadyUserSoftDeleted() {
      // given
      User user = new User("test", "test@gmail.com", "qwer1234!", Role.USER);
      userRepository.save(user);
      entityManager.flush();
      entityManager.clear();
      UUID userId = user.getId();
      user.delete();
      userRepository.save(user);
      entityManager.flush();
      entityManager.clear();

      // when
      Optional<User> result = userRepository.findActiveById(userId);

      // then
      assertThat(result).isEmpty();
    }
  }
}