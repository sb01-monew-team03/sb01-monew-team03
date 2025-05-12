package team03.monew.service.s3;

import java.nio.file.Path;
import java.time.LocalDate;

public interface S3Service {

    void upload(Path path, LocalDate date);

    Path download(LocalDate date);
}