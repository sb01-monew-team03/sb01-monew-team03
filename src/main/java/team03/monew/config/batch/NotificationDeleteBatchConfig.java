package team03.monew.config.batch;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
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

  private final MeterRegistry meterRegistry;

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
      Timer.Sample taskletTimerSample = Timer.start(meterRegistry);

      try {
        Instant oneweek = Instant.now().minus(7, ChronoUnit.DAYS);
        int deleteCount = notificationRepository.deleteAllConfirmNotification(oneweek);

        Counter.builder("batch.notification.deleted.count")
                .description("배치 작업으로 삭제된 알림 수")
                    .tag("type", "confirmed")
                        .register(meterRegistry)
                            .increment(deleteCount);
        log.info("일주일 경과된 확인한 알림 모두 삭제 완료: 삭제 개수 = {}", deleteCount);
        return RepeatStatus.FINISHED;
      } finally {
        taskletTimerSample.stop(Timer.builder("batch.notification.tasklet.duration")
            .description("알림 삭제 Tasklet 실행 시간")
            .tag("step", "deleteNotificationStep")
            .register(meterRegistry));
      }
    };
  }

  @Bean
  public JobExecutionListener notificationJobExecutionListener() {
    return new JobExecutionListener() {

      private Timer.Sample jobTimerSample;

      @Override
      public void beforeJob(JobExecution jobExecution) {
        jobTimerSample = Timer.start(meterRegistry);
        log.info("알림 삭제 배치 작업 시작: {}", jobExecution.getJobInstance().getJobName());
      }

      @Override
      public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        String status = jobExecution.getStatus().name();
        jobTimerSample.stop(Timer.builder("batch.notification.job.duration")
            .description("알림 삭제 배치 작업 실행 시간")
            .tag("job", jobName)
            .tag("status", status)
            .register(meterRegistry));

        log.info("알림 삭제 배치 작업 완료: {} - 상태: {}",
            jobName,
            status);
      }
    };
  }

}
