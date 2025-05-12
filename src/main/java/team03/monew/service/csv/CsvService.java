package team03.monew.service.csv;

import java.nio.file.Path;
import java.util.List;
import team03.monew.entity.article.Article;

public interface CsvService {

    Path exportArticlesToCsv(Path filePath, List<Article> articles);
}
