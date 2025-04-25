package team03.monew.dto.notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationFindRequest(
    String cursor,
    Instant after,
    Integer limit,
    UUID userId
) {

}
