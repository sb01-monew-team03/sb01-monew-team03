package team03.monew.service.article;

import java.time.LocalDate;
import team03.monew.dto.article.ArticleRestoreDto;

public interface ArticleRestoreService {

    ArticleRestoreDto restore(LocalDate from, LocalDate to);
}
