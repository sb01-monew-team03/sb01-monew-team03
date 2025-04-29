package team03.monew.config.api.interest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import team03.monew.dto.interest.SubscriptionDto;
import team03.monew.util.exception.ErrorResponse;

@Tag(name = "관심사 구독 API")
public interface SubscriptionApi {

  // POST /api/interests/{interestId}/subscriptions
  @Operation(
      summary = "관심사 구독",
      description = "관심사를 구독합니다."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "등록 성공",
          content = @Content(
              schema = @Schema(
                  implementation = SubscriptionDto.class
              )
          )
      ),
      @ApiResponse(
          responseCode = "409",
          description = "이미 구독중인 관심사",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "서버 오류",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "503",
          description = "시도 횟수 초과",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  ResponseEntity<SubscriptionDto> create(UUID interestId, UUID userId);

  // DELETE /api/interests/{interestId}/subscriptions
  @Operation(
      summary = "관심사 구독 취소",
      description = "관심사 구독을 취소합니다."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "204",
          description = "구독 취소 성공"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "구독하지 않은 관심사",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "서버 오류",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "503",
          description = "시도 횟수 초과",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  ResponseEntity<Void> delete(UUID interestId, UUID userId);
}
