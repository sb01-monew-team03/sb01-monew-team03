package team03.monew.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

  @Value("${monew.storage.s3.access-key}")
  private String accessKey;

  @Value("${monew.storage.s3.secret-key}")
  private String secretKey;

  @Value("${monew.storage.s3.region}")
  private String region;

  @Bean
  public AmazonS3Client amazonS3Client() {

    AWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);

    return (AmazonS3Client) AmazonS3ClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
        .withRegion(region)
        .build();
  }
}
