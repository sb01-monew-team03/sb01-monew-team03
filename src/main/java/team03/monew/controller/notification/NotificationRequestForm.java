package team03.monew.controller.notification;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequestForm {
  private String cursor;
  private Instant after;
  private Integer limit;
  private UUID userId;
}
