package team03.monew.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.dto.user.UserDto;
import team03.monew.dto.user.UserLoginRequest;
import team03.monew.mapper.UserMapper;
import team03.monew.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserDto login(UserLoginRequest request) {
    return null;
  }
}
