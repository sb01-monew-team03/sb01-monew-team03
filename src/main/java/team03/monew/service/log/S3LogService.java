package team03.monew.service.log;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class S3LogService {

  private final AmazonS3Client amazonS3Client;

  @Value("${monew.storage.s3.bucket}")
  private String bucketName;

  @Value("${log.file.path}")
  private String logFilePath;

  public S3LogService(AmazonS3Client amazonS3Client) {
    this.amazonS3Client = amazonS3Client;
  }

  // 주기적으로 S3에 Log 업로드
  @Scheduled(cron = "0 10 0 * * *")  // 매일 0시 10분에 실행
  public void uploadLogFileToS3() {

    String yesterday = LocalDate.now().minusDays(1)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    String fileName = "/logFile." + yesterday + ".log";
    File logFile = new File(logFilePath + fileName);

    String folderName = logFilePath.split("./")[1];

    if (logFile.exists()) {
      PutObjectRequest request = new PutObjectRequest(bucketName, folderName + fileName, logFile);
      amazonS3Client.putObject(request);
      log.info("로그 파일 S3 업로드 성공: {}", logFile.getName());
    } else {
      log.debug("업로드할 로그 파일 없음: {}", logFile.getName());
    }
  }
}
