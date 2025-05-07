package team03.monew.event.subscription;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team03.monew.entity.interest.Interest;

@Getter
@RequiredArgsConstructor
public class SubscriptionDeleteEvent {

  private final Interest interest;
}
