package team03.monew.controller.user;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team03.monew.dto.user.UserDto;
import team03.monew.dto.user.UserRegisterRequest;
import team03.monew.dto.user.UserUpdateRequest;
import team03.monew.service.user.UserService;
import team03.monew.util.exception.user.ForbiddenException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @PostMapping()
  public ResponseEntity<UserDto> create(@RequestBody @Valid UserRegisterRequest request) {
    log.info("사용자 생성 요청: {}", request);
    UserDto userDto = userService.create(request);
    log.debug("사용자 생성 응답: {}", userDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> update(@PathVariable UUID userId,
      @RequestBody @Valid UserUpdateRequest request,
      @RequestHeader("MoNew-Request-User-ID") UUID requestUserId) {
    if (!requestUserId.equals(userId)) {
      throw ForbiddenException.WrongUserId();
    }

    log.info("사용자 수정 요청: userId={}, request={}", userId, request);
    UserDto userDto = userService.update(userId, request);
    log.debug("사용자 수정 응답: {}", userDto);
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> softDelete(@PathVariable UUID userId,
      @RequestHeader("MoNew-Request-User-ID") UUID requestUserId) {
    if (!requestUserId.equals(userId)) {
      throw ForbiddenException.WrongUserId();
    }

    log.info("사용자 논리 삭제 요청: userId={}", userId);
    userService.softDelete(userId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @DeleteMapping("/{userId}/hard")
  public ResponseEntity<Void> hardDelete(@PathVariable UUID userId,
      @RequestHeader("MoNew-Request-User-ID") UUID requestUserId) {
    if (!requestUserId.equals(userId)) {
      throw ForbiddenException.WrongUserId();
    }

    log.info("사용자 물리 삭제 요청: userId={}", userId);
    userService.hardDelete(userId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
