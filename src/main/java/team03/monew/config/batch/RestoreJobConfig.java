package team03.monew.config.batch;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestoreJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ArticleRestoreService articleRestoreService;

    @Bean(name = "articleRestoreJob")
    public Job articleRestoreJob() {
        return new JobBuilder("articleRestoreJob", jobRepository)
            .start(restoreStep(null, null))
            .build();
    }

    @Bean
    @StepScope
    public Step restoreStep(
        @Value("#{jobParameters['from']}") String from,
        @Value("#{jobParameters['to']}") String to
    ) {
        return new StepBuilder("restoreStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                LocalDate fromDate = LocalDate.parse(from);
                LocalDate toDate = LocalDate.parse(to);

                log.info("\uD83D\uDD04 복구 작업 시작 - 기간: {} ~ {}", fromDate, toDate);

                ArticleRestoreResultDto result = articleRestoreService.restore(fromDate, toDate);
                log.info("\u2705 복구 완료 - 날짜: {}, 복구된 기사 수: {}", result.restoreDate(),
                    result.restoredArticleCount());

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
}
