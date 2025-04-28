package team03.monew.config.api.interest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.dto.interest.InterestDto;
import team03.monew.dto.interest.InterestFindRequest;
import team03.monew.dto.interest.InterestRegisterRequest;
import team03.monew.dto.interest.InterestUpdateRequest;
import team03.monew.util.exception.ErrorResponse;

@Tag(name = "관심사 관리 API")
public interface InterestApi {

  // POST /api/interests
  @Operation(
      summary = "관심사 등록",
      description = "새로운 관심사를 등록합니다."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "등록 성공",
          content = @Content(
              schema = @Schema(
                  implementation = InterestDto.class
              )
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청(관심사명 누락, 키워드 누락 등)",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "403",
          description = "관리자가 아님",
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
      )
  })
  ResponseEntity<InterestDto> create(InterestRegisterRequest request, HttpSession session);

  // PATCH /api/interests/{interestId}
  @Operation(
      summary = "관심사 정보 수정",
      description = "관심사 키워드를 수정합니다."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "수정 성공",
          content = @Content(
              schema = @Schema(implementation = InterestDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청(키워드 누락 등)",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "403",
          description = "관리자가 아님",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "해당 관심사가 없음",
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
      )
  })
  ResponseEntity<InterestDto> update(UUID interestId, InterestUpdateRequest request,
      UUID userId, HttpSession session
  );

  // DELETE /api/interests/{interestId}
  @Operation(
      summary = "관심사 물리 삭제",
      description = "관심사를 삭제합니다."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "204",
          description = "삭제 성공"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청(유효하지 않은 uuid 작성 등)",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "403",
          description = "관리자가 아님",
          content = @Content(
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "해당 관심사가 없음",
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
      )
  })
  ResponseEntity<Void> delete(UUID interestId, HttpSession session);

  // GET /api/interests
  @Operation(
      summary = "관심사 목록 조회",
      description = "요청 조건에 맞는 관심사를 한번에 조회합니다."
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
          description = "잘못된 요청(정렬 속성 누락, 정렬 방향 누락 등)",
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
      )
  })
  ResponseEntity<CursorPageResponse<InterestDto>> find(InterestFindRequest request, UUID userId);
}
