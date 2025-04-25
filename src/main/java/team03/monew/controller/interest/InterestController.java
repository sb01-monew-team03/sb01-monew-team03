package team03.monew.controller.interest;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.dto.interest.InterestDto;
import team03.monew.dto.interest.InterestFindRequest;
import team03.monew.dto.interest.InterestRegisterRequest;
import team03.monew.dto.interest.InterestUpdateRequest;
import team03.monew.service.interest.InterestService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interests")
public class InterestController {

  private final InterestService interestService;

  // 관심사 등록
  @PostMapping
  public ResponseEntity<InterestDto> create(
      @RequestBody @Valid InterestRegisterRequest request,
      @RequestHeader(value = "Monew-Request-User-ID") UUID userId
  ) {

    log.info("관심사 등록 요청: {}", request);

    InterestDto interestDto = interestService.create(request);

    log.debug("관심사 생성 응답: {}", interestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(interestDto);
  }

  // 관심사 정보 수정
  @PatchMapping("/{interestId}")
  public ResponseEntity<InterestDto> update(
      @PathVariable UUID interestId,
      @RequestBody @Valid InterestUpdateRequest request,
      @RequestHeader(value = "Monew-Request-User-ID") UUID userId
  ) {

    log.info("관심사 정보 수정 요청: interestId={}", interestId);

    InterestDto interestDto = interestService.update(interestId, request, userId);

    log.debug("관심사 정보 수정 응답: {}", interestDto);

    return ResponseEntity.status(HttpStatus.OK).body(interestDto);
  }

  // 관심사 물리 삭제
  @DeleteMapping("/{interestId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID interestId,
      @RequestHeader(value = "Monew-Request-User-ID") UUID userId
  ) {

    log.info("관심사 물리 삭제 요청: interestId={}", interestId);

    interestService.delete(interestId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  // 관심사 목록 조회
  @GetMapping
  public ResponseEntity<CursorPageResponse<InterestDto>> find(
      @ModelAttribute @Valid InterestFindRequest request,
      @RequestHeader(value = "Monew-Request-User-ID") UUID userId
  ) {

    log.info("관심사 목록 조회 요청: userId={}, request={}", userId, request);

    CursorPageResponse<InterestDto> response = interestService.find(request, userId);

    log.debug("관심사 목록 조회 응답: contentSize={}, nextCursor={}, totalElements={}",
        response.size(), response.nextCursor(), response.totalElements());

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
