package team03.monew.dto.interest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SubscriptionDto(
    UUID id,
    UUID interestId,
    String interestName,
    List<String> interestKeywords,
    long interestSubscriberCount,
    Instant createdAt
) {

}
