package team03.monew.controller.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.dto.notification.NotificationDto;
import team03.monew.dto.notification.NotificationFindRequest;
import team03.monew.dto.notification.ResourceType;
import team03.monew.service.notification.NotificationService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private NotificationController notificationController;

  private UUID userId;
  private UUID notificationId;
  private NotificationFindRequest findRequest;
  private NotificationDto notificationDto;
  private CursorPageResponse<NotificationDto> pageResponse;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    notificationId = UUID.randomUUID();

    // Setup test data
    findRequest = new NotificationFindRequest(
        "2023-01-01T00:00:00Z",
        Instant.parse("2023-01-01T00:00:00Z"),
        10,
        userId
    );

    notificationDto = new NotificationDto(
        notificationId,
        Instant.parse("2023-01-01T12:00:00Z"),
        Instant.parse("2023-01-01T12:00:00Z"),
        false,
        userId,
        "Test notification content",
        ResourceType.INTEREST,
        UUID.randomUUID()
    );

    pageResponse = new CursorPageResponse<>(
        List.of(notificationDto),
        "2023-01-01T11:00:00Z",
        Instant.parse("2023-01-01T11:00:00Z"),
        1,
        10,
        true
    );
  }

  @Test
  @DisplayName("알림 목록 조회 테스트")
  void findAll_ShouldReturnNotificationList() {
    // Given
    doReturn(pageResponse).when(notificationService)
        .findAll(any(UUID.class), any(), any(), any(Integer.class));

    // When
    ResponseEntity<CursorPageResponse<NotificationDto>> response = notificationController.findAll(
        UUID.fromString("c86de1ca-b717-4ae8-ae23-ab1af29e5627"), null, null, 50);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    verify(notificationService, times(1)).findAll(any(UUID.class), any(), any(), any(Integer.class));
  }

  @Test
  @DisplayName("단일 알림 확인 상태 업데이트 테스트")
  void update_ShouldUpdateNotificationStatus() {
    // Given
    doNothing().when(notificationService).readNotification(notificationId, userId);

    // When
    ResponseEntity<Void> response = notificationController.update(notificationId, userId);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(notificationService, times(1)).readNotification(notificationId, userId);
  }

  @Test
  @DisplayName("모든 알림 확인 상태 업데이트 테스트")
  void updateAll_ShouldUpdateAllNotificationStatus() {
    // Given
    doNothing().when(notificationService).readAllNotification(userId);

    // When
    ResponseEntity<Void> response = notificationController.updateAll(userId);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(notificationService, times(1)).readAllNotification(userId);
  }
}