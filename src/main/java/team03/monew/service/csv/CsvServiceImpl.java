package team03.monew.service.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import team03.monew.entity.article.Article;

@Slf4j
@Service
public class CsvServiceImpl implements CsvService {

    @Override
    public Path exportArticlesToCsv(Path filePath, List<Article> articles) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath);
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("id", "source", "title", "summary", "sourceUrl", "publishDate",
                    "interestId"))
        ) {
            for (Article article : articles) {
                printer.printRecord(
                    article.getId(),
                    article.getSource(),
                    article.getTitle(),
                    article.getSummary(),
                    article.getOriginalLink(),
                    article.getPublishedAt(),
                    article.getInterests().size()
                );
            }
        } catch (IOException e) {
            log.error("CSV 백업 파일 생성 실패 - 경로: {}, 에러: {}", filePath, e.getMessage(), e);
            throw new RuntimeException("CSV 백업 파일 생성 중 오류 발생", e);
        }

        log.info("CSV 파일 성공적으로 생성됨 - 경로: {}", filePath);
        return filePath;
    }
}