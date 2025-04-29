package team03.monew.service.activity;

import java.util.UUID;
import team03.monew.dto.user.UserActivityDto;

public interface ActivityService {

  UserActivityDto findUserActivity(UUID userId);

}
