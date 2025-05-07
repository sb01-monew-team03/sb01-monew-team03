package team03.monew.config.batch;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import team03.monew.repository.notification.NotificationRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NotificationDeleteBatchConfig {

  @Bean
  public Job deleteNotificationJob(JobRepository jobRepository,
      Step deleteNotificationStep) {
    return new JobBuilder("deleteNotificationJob", jobRepository)
        .start(deleteNotificationStep)
        .build();

  }

  @Bean
  public Step deleteNotificationStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      Tasklet deleteNotificationTasklet, NotificationRepository notificationRepository) {
    return new StepBuilder("deleteNotificationStep", jobRepository)
        .tasklet(deleteNotificationTasklet, transactionManager)
        .build();
  }

  @Bean
  public Tasklet deleteNotificationTasklet(NotificationRepository notificationRepository) {
    return (contribution, chunkContext) -> {
      Instant oneweek = Instant.now().minus(7, ChronoUnit.DAYS);
      int deleteCount = notificationRepository.deleteByConfirmedIsTrueAndCreatedAtBefore(oneweek);
      log.info("일주일 경과된 확인한 알림 모두 삭제 완료: 삭제 개수 = {}", deleteCount);
      return RepeatStatus.FINISHED;
    };
  }

}
