package team03.monew.event.subscription;

import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Subscription;

public record SubscriptionCreateEvent(
    Interest interest,
    Subscription subscription) {

}
