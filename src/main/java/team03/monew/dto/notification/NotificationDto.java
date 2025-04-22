package team03.monew.dto.notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    Boolean confirmed,
    UUID userId,
    String content,
    ResourceType type,
    UUID resourceId
) {

}