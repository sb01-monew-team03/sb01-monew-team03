package team03.monew.event.subscription;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import team03.monew.entity.interest.Interest;
import team03.monew.service.interest.InterestService;

@Component
@RequiredArgsConstructor
public class SubscriptionEventListener {

  private final InterestService interestService;

  @EventListener
  public void handleSubscriptionCreate(SubscriptionCreateEvent event) {
    Interest interest = event.getInterest();
    interestService.updateSubscriberCount(interest, true);
  }

  @EventListener
  public void handleSubscriptionDelete(SubscriptionDeleteEvent event) {
    Interest interest = event.getInterest();
    interestService.updateSubscriberCount(interest, false);
  }
}
