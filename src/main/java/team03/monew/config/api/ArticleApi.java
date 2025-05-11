package team03.monew.config.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import team03.monew.dto.article.ArticleDto;
import team03.monew.dto.article.ArticleFindRequest;
import team03.monew.dto.article.ArticleRestoreDto;
import team03.monew.dto.article.ArticleViewDto;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.util.exception.ErrorResponse;

@Tag(name = "뉴스 기사 관리 API")
public interface ArticleApi {

    // POST /api/articles/{articleId}/article-views
    @Operation(summary = "기사 뷰 등록", description = "기사 뷰를 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "기사 뷰 등록 성공",
            content = @Content(schema = @Schema(implementation = ArticleViewDto.class))),
        @ApiResponse(responseCode = "404", description = "댓글 정보 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ArticleViewDto> createView(UUID articleId, UUID userId);

    // GET /api/articles
    @Operation(summary = "뉴스 기사 목록 조회", description = "조건에 맞는 뉴스 기사 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CursorPageResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류 등)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<CursorPageResponse<ArticleDto>> find(UUID userId, ArticleFindRequest request);

    // GET /api/articles/sources
    @Operation(summary = "출처 목록 조회", description = "출처 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<String>> findSources();

    // GET /api/articles/restore
    @Operation(summary = "뉴스 복구", description = "유실된 뉴스 기사를 복구합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "복구 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArticleRestoreDto.class)))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<ArticleRestoreDto>> restore(Instant from, Instant to);

    // DELETE /api/articles/{articleId}
    @Operation(summary = "뉴스 기사 논리 삭제", description = "뉴스 기사를 논리적으로 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "논리 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "뉴스 기사 정보 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> softDelete(UUID articleId);

    // DELETE /api/articles/{articleId}/hard
    @Operation(summary = "뉴스 기사 물리 삭제", description = "뉴스 기사를 물리적으로 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "뉴스 기사 정보 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> hardDelete(UUID articleId);
}
