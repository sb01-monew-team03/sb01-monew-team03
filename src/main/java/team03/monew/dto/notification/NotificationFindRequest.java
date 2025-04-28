package team03.monew.dto.notification;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public record NotificationFindRequest(
    String cursor,
    Instant after,
    @NotBlank(message = "limit는 필수입니다.")
    Integer limit,
    @NotBlank(message = "user ID는 필수입니다.")
    UUID userId
) {

}