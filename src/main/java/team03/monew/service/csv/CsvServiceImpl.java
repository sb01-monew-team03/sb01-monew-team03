package team03.monew.service.csv;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public void exportArticlesToCsv(Path filePath, List<Article> articles) {
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
    }

    @Override
    public List<Article> importArticlesFromCsv(Path filePath) {
        List<Article> articles = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
            String[] header = reader.readNext();
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length >= 5) {
                    String source = nextLine[0];
                    String originalLink = nextLine[1];
                    String title = nextLine[2];
                    String summary = nextLine[3];
                    LocalDateTime publishedAt = LocalDateTime.parse(nextLine[4]);

                    Article article = new Article(source, originalLink, title, summary,
                        publishedAt);
                    articles.add(article);
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("CSV 복원 중 오류 발생", e);
        }

        log.info("CSV 파일에서 복원된 기사 수: {}", articles.size());
        return articles;
    }
}