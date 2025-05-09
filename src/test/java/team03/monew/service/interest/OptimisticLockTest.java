package team03.monew.service.interest;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import team03.monew.dto.interest.InterestDto;
import team03.monew.dto.interest.InterestRegisterRequest;
import team03.monew.entity.interest.Interest;

//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//@TestInstance(Lifecycle.PER_CLASS)
//public class OptimisticLockTest {
//
//  @Autowired
//  private InterestService interestService;
//
//  @Autowired
//  private InterestReader interestReader;
//
//  private Interest interest;
//
//  @BeforeEach
//  void setUp() {
//    InterestRegisterRequest request = new InterestRegisterRequest(
//        UUID.randomUUID().toString(),
//        List.of("test")
//    );
//    InterestDto interestDto = interestService.create(request);
//    interest = interestReader.getInterestEntityById(UUID.fromString(interestDto.id()));
//  }
//
//  @RepeatedTest(10)
//  @DisplayName("[InterestService - updateSubscriberCount()] 낙관적 락 테스트")
//  void testOptimisticLock() throws InterruptedException {
//
//    int threadCount = 5;
//    CountDownLatch latch = new CountDownLatch(threadCount);
//
//    Runnable task = () -> {
//      try {
//        UUID interestId = UUID.randomUUID();
//        interestService.updateSubscriberCount(interest, true);
//      } catch (Exception e) {
//        e.printStackTrace();
//      } finally {
//        latch.countDown();
//        ;
//      }
//    };
//
//    for (int i = 0; i < threadCount; i++) {
//      new Thread(task).start();
//    }
//
//    latch.await();
//  }
//}
