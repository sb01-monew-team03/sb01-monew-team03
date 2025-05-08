package team03.monew.service.notification.scheduler;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationScheduler {

  private final JobLauncher jobLauncher;
  private final Job deleteNotificationJob;
  private final MeterRegistry meterRegistry;
  private final Counter scheduledRunCounter;

  public NotificationScheduler(JobLauncher jobLauncher,
  @Qualifier("deleteNotificationJob") Job deleteNotificationJob,
      MeterRegistry meterRegistry) {
    this.jobLauncher = jobLauncher;
    this.deleteNotificationJob = deleteNotificationJob;
    this.meterRegistry = meterRegistry;
    this.scheduledRunCounter = Counter.builder("batch.notification.scheduler.runs")
        .description("알림 삭제 스케줄러 실행 횟수")
        .tag("job", "deleteNotificationJob")
        .register(meterRegistry);
  }

  @Scheduled(cron = "0 0 4 * * *")
  public void runJob() throws Exception {
    JobParameters parameters = new JobParametersBuilder()
        .addLong("timestamp", System.currentTimeMillis())
        .toJobParameters();

    log.info("알림 삭제 배치 작업 스케줄링 실행");
    scheduledRunCounter.increment();

    try {
      jobLauncher.run(deleteNotificationJob, parameters);
    } catch (Exception e) {
      Counter.builder("batch.notification.scheduler.error")
          .description("알림 삭제 배치 작업 스케줄링 실행 오류")
          .tag("job", "deleteNotificationJob")
          .tag("error", e.getMessage())
          .register(meterRegistry)
          .increment();

      log.error("알림 삭제 배치 작업 스케줄링 중 오류 발생: {}", e.getMessage());
      throw e;
    }
  }
}
