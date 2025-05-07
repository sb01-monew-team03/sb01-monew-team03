package team03.monew.service.notification.scheduler;

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

  public NotificationScheduler(JobLauncher jobLauncher,
  @Qualifier("deleteNotificationJob") Job deleteNotificationJob) {
    this.jobLauncher = jobLauncher;
    this.deleteNotificationJob = deleteNotificationJob;
  }

  @Scheduled(cron = "0 30 2 * * *")
  public void runJob() throws Exception {
    JobParameters parameters = new JobParametersBuilder()
        .addLong("timestamp", System.currentTimeMillis())
        .toJobParameters();

    log.info("");
    jobLauncher.run(deleteNotificationJob, parameters);
  }
}
