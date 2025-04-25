package team03.monew.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    String email,
    String nickname,
    Instant createdAt,
    String role
) {

}
