package team03.monew.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team03.monew.dto.user.UserDto;
import team03.monew.dto.user.UserLoginRequest;
import team03.monew.service.user.AuthService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@RequestBody @Valid UserLoginRequest request) {
    log.info("로그인 요청: email={}", request.email());
    UserDto userDto = authService.login(request);
    log.debug("로그인 응답: {}", userDto);
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }
}
