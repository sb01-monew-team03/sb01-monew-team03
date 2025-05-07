package team03.monew.controller.activity;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team03.monew.dto.user.UserActivityDto;
import team03.monew.service.activity.ActivityService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-activities")
public class ActivityController {

  private final ActivityService activityService;

  @GetMapping("/{userId}")
  public ResponseEntity<UserActivityDto> find(@PathVariable UUID userId) {
    log.info("사용자 활동 내역 조회 요청: User Id: {}", userId);

    UserActivityDto userActivityDto = activityService.findUserActivity(userId);

    log.debug("사용자 활동 내역 조회 응답: User Id: {}", userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userActivityDto);
  }
}
