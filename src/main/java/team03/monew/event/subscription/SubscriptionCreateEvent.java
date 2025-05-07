package team03.monew.event.subscription;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team03.monew.entity.interest.Interest;

@Getter
@RequiredArgsConstructor
public class SubscriptionCreateEvent {

  private final Interest interest;
}
