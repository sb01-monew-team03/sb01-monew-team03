package team03.monew.controller.interest;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team03.monew.config.api.interest.SubscriptionApi;
import team03.monew.dto.interest.SubscriptionDto;
import team03.monew.service.interest.SubscriptionService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interests")
public class SubscriptionController implements SubscriptionApi {

  private final SubscriptionService subscriptionService;

  // 관심사 구독
  @PostMapping("/{interestId}/subscriptions")
  public ResponseEntity<SubscriptionDto> create(
      @PathVariable UUID interestId,
      @RequestHeader(value = "Monew-Request-User-ID") UUID userId
  ) {

    log.info("관심사 구독 요청: interestId={}, userId={}", interestId, userId);

    SubscriptionDto subscriptionDto = subscriptionService.create(userId, interestId);

    log.debug("관심사 구독 응답: {}", subscriptionDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionDto);
  }

  // 관심사 구독 취소
  @DeleteMapping("/{interestId}/subscriptions")
  public ResponseEntity<Void> delete(
      @PathVariable UUID interestId,
      @RequestHeader(value = "Monew-Request-User-ID") UUID userId
  ) {

    log.info("관심사 구독 요청: interestId={}, userId={}", interestId, userId);

    subscriptionService.delete(userId, interestId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
