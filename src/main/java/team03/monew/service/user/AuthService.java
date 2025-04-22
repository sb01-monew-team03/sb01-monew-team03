package team03.monew.service.user;

import team03.monew.dto.user.UserDto;
import team03.monew.dto.user.UserLoginRequest;

public interface AuthService {

  // 로그인
  UserDto login(UserLoginRequest request);

}
