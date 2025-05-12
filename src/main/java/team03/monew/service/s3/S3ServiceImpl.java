package team03.monew.service.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${monew.storage.s3.bucket}")
    private String bucketName;

    @Override
    public void upload(Path path, LocalDate date) {
        String key = generateKey(date);
        PutObjectRequest request = new PutObjectRequest(bucketName, key, path.toFile());
        amazonS3Client.putObject(request);
        log.info("S3 업로드 완료: {}", key);
    }

    @Override
    public Path download(LocalDate date) {
        String key = generateKey(date);
        Path downloadPath = Path.of(System.getProperty("java.io.tmpdir"), key.replace("/", "_"));
        amazonS3Client.getObject(new GetObjectRequest(bucketName, key), downloadPath.toFile());
        log.info("S3 파일 다운로드 완료: {}", key);
        return downloadPath;
    }

    private String generateKey(LocalDate date) {
        String datePath = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = "articles_" + date.format(DateTimeFormatter.ISO_DATE) + ".csv";
        return "backup/" + datePath + "/" + fileName; // directly define prefix here
    }
}