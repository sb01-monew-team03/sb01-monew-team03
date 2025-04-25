package team03.monew.controller.notification;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team03.monew.config.api.NotificationApi;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.dto.notification.NotificationDto;
import team03.monew.service.notification.NotificationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationApi {

  private final NotificationService notificationService;

  @Override
  @GetMapping
  public ResponseEntity<CursorPageResponse<NotificationDto>> findAll (
      @ModelAttribute NotificationRequestForm request) {
    log.info("알림 조회 요청: {}", request);

    CursorPageResponse<NotificationDto> notificationDtos = notificationService.findAll(request);
    log.debug("알림 조회 응답: {}", notificationDtos);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(notificationDtos);

  }

  @Override
  @PatchMapping
  public ResponseEntity<Void> updateAll(@RequestHeader UUID userId) {
    log.info("모든 알림 확인 여부 수정 요청 - 사용자 ID: {}", userId);

    notificationService.readAllNotification(userId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .build();
  }

  @Override
  @PatchMapping("/{notificationId}")
  public ResponseEntity<Void> update(
      @PathVariable UUID notificationId,
      @RequestHeader UUID userId) {
    log.info("알림 확인 여부 수정 요청 - 알림 ID: {}", notificationId);

    notificationService.readNotification(notificationId, userId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .build();
  }

}
