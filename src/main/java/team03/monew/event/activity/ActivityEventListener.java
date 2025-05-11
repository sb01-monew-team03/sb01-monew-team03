package team03.monew.event.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import team03.monew.event.comment.CommentCreateEvent;
import team03.monew.event.subscription.SubscriptionCreateEvent;

@Component
@RequiredArgsConstructor
public class ActivityEventListener {

  private final ActivityEventHandler activityEventHandler;

  @EventListener
  public void handleCommentCreatedEvent(CommentCreateEvent event) {
    activityEventHandler.handleCommentCreated(event.comment());
  }

  @EventListener
  public void handleSubscriptionCreatedEvent(SubscriptionCreateEvent event) {
    activityEventHandler.handleSubscriptionCreated(event.subscription());
  }

}