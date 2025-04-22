package team03.monew.dto.notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Boolean confirmed,
    UUID userId,
    String content,
    ResourceType type,
    UUID resourceId
) {

}