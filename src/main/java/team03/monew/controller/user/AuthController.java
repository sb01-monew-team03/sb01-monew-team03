package team03.monew.controller.user;

import jakarta.servlet.http.HttpSession;
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
  public ResponseEntity<UserDto> login(
      @RequestBody @Valid UserLoginRequest request,
      HttpSession session) {
    log.info("로그인 요청: email={}", request.email());
    UserDto userDto = authService.login(request);
    log.debug("로그인 응답: {}", userDto);

    log.debug("세션 저장: userId={}, role={}", userDto.id(), userDto.role());
    session.setAttribute("userId", userDto.id());
    session.setAttribute("role", userDto.role());
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }
}
