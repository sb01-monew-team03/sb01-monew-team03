package team03.monew.config.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.dto.notification.NotificationDto;
import team03.monew.dto.notification.NotificationFindRequest;

@Tag(name = "알림 관리 API")
public interface NotificationApi {

  //Get /api/notifications
  @Operation(
      summary = "알림 목록 조회",
      description = "알림 목록을 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "조회 성공",
          content = @Content(
              schema = @Schema(
                  implementation = CursorPageResponse.class
              )
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청(정렬 기준 오류, 페이지네이션 파라미터 오류 등)",
          content = @Content(
              schema = @Schema(implementation = CursorPageResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "서버 내부 오류",
          content = @Content(
              schema = @Schema(implementation = CursorPageResponse.class)
          )
      )
  })
  ResponseEntity<CursorPageResponse<NotificationDto>> findAll(NotificationFindRequest request);

  //PATCH /api/notifications
  @Operation(
      summary = "전체 알림 확인",
      description = "전체 알림을 한번에 확인합니다."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "전체 알림 확인 성공"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 (입력값 검증 실패)"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "사용자 정보 없음"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "서버 내부 오류"
      ),
  })
  ResponseEntity<Void> updateAll(
      @Parameter(description = "요청자 ID", required = true) UUID userId);

  //PATCH /api/notifications/{notificationId}
  @Operation(
      summary = "알림 확인",
      description = "알림을 확인합니다."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "알림 확인 성공"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 (입력값 검증 실패)"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "사용자 정보 없음"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "서버 내부 오류"
      ),
  })
  ResponseEntity<Void> update(@Parameter(description = "알림 ID", required = true) UUID notificationId,
      @Parameter(description = "요청자 ID", required = true) UUID userId);

}
