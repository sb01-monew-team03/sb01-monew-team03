package team03.monew.service.activity;

import java.util.UUID;
import team03.monew.dto.user.ActivityDto;

public interface ActivityService {

  ActivityDto findUserActivity(UUID userId);

}
