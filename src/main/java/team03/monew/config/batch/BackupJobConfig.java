package team03.monew.config.batch;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import team03.monew.entity.article.Article;
import team03.monew.repository.article.ArticleRepository;
import team03.monew.service.csv.CsvService;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BackupJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ArticleRepository articleRepository;
    private final CsvService csvService;
    private final S3Service s3Service;

    @Bean(name = "articleBackupJob")
    public Job articleBackupJob() {
        return new JobBuilder("articleBackupJob", jobRepository)
            .start(backupStep(null))
            .build();
    }

    @Bean
    @StepScope
    public Step backupStep(@Value("#{jobParameters['backupDate']}") String backupDate) {
        return new StepBuilder("backupStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                LocalDate targetDate = (backupDate != null)
                    ? LocalDate.parse(backupDate)
                    : LocalDate.now().minusDays(1);

                log.info("\uD83D\uDCE6 백업 작업 시작 - 대상 날짜: {}", targetDate);

                LocalDateTime startTime = targetDate.atStartOfDay();
                LocalDateTime endTime = targetDate.plusDays(1).atStartOfDay();

                List<Article> articles = articleRepository.findAllByPublishedAtBetween(startTime,
                    endTime);
                if (articles.isEmpty()) {
                    log.info("\u26A0\uFE0F 백업 대상 기사가 없습니다 - 날짜: {}", targetDate);
                    return RepeatStatus.FINISHED;
                }

                String fileName = "articles_" + targetDate + ".csv";
                Path filePath = Path.of(System.getProperty("java.io.tmpdir"), fileName);

                csvService.exportArticlesToCsv(filePath, articles);
                log.info("\u2705 CSV 파일 생성 완료 - 경로: {}, 기사 수: {}", filePath, articles.size());

                s3Service.upload(filePath, targetDate);
                log.info("\u2601\uFE0F S3 업로드 완료 - 파일 이름: {}", fileName);

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
}

