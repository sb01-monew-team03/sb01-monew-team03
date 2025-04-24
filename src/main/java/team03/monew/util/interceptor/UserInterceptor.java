package team03.monew.util.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import team03.monew.entity.user.User;
import team03.monew.service.user.UserService;
import team03.monew.util.exception.user.ForbiddenException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserInterceptor implements HandlerInterceptor {

  private final UserService userService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception {
    log.debug("권한 확인 시작");
    String headerUserId = request.getHeader("MoNew-Request-User-ID");
    String pathUserId = request.getRequestURI().split("/api/users/")[1].split("/")[0];
    User headerUser = userService.findUserById(UUID.fromString(headerUserId));

    if (!headerUserId.equals(pathUserId) && !headerUser.getRole().toString().equals("ADMIN")) {
      throw ForbiddenException.WrongUserId();
    }
    return true;
  }

}
