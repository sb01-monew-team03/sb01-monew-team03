package team03.monew.service.article;

import java.util.List;
import java.util.UUID;
import team03.monew.dto.article.ArticleCreateRequest;
import team03.monew.dto.article.ArticleDto;

public interface ArticleService {

    // 기사 등록
    ArticleDto create(ArticleCreateRequest request);

    // 단일 기사 조회
    ArticleDto findById(UUID articleId);

    // 기사 검색
    //List<ArticleDto> search();

    // 논리 삭제
    void softDelete(UUID articleId);

    // 물리 삭제
    void hardDelete(UUID articleId);
}
