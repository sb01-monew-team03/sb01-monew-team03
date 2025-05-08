package team03.monew.repository.interest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import team03.monew.config.JpaConfig;
import team03.monew.config.QueryDslConfig;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Subscription;
import team03.monew.entity.user.User;
import team03.monew.entity.user.User.Role;
import team03.monew.repository.interest.subscription.SubscriptionRepository;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaConfig.class})
@EntityScan(basePackageClasses = {Interest.class, User.class, Subscription.class})
@EnableJpaRepositories(basePackageClasses = SubscriptionRepository.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class SubscriptionRepositoryTest {

  @Autowired
  SubscriptionRepository subscriptionRepository;

  @Autowired
  TestEntityManager em;

  User user;
  Interest interest1;
  Interest interest2;
  Interest interest3;
  Subscription saved1;
  Subscription saved2;

  @BeforeEach
  void setUp() {
    user = new User("nickname", "email", "password", Role.USER);
    em.persist(user);

    interest1 = new Interest("관심사1");
    interest2 = new Interest("관심사2");
    interest3 = new Interest("관심사3");
    em.persist(interest1);
    em.persist(interest2);
    em.persist(interest3);

    Subscription subscription1 = new Subscription(user, interest1);
    saved1 = subscriptionRepository.save(subscription1);
    Subscription subscription2 = new Subscription(user, interest3);
    saved2 = subscriptionRepository.save(subscription2);
  }

  @Test
  @DisplayName("구독중인 관심사 아이디 검색 테스트")
  void findInterestIdsByUserIdTest() {

    // when
    List<UUID> interestIds = subscriptionRepository.findInterestIdsByUserId(user.getId());

    // then
    assertThat(interestIds).hasSize(2);
    assertThat(interestIds).contains(interest1.getId());
    assertThat(interestIds).contains(interest3.getId());
  }
}
