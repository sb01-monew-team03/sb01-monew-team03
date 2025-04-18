package team03.monew.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.dto.user.UserDto;
import team03.monew.dto.user.UserLoginRequest;
import team03.monew.entity.user.User;
import team03.monew.mapper.user.UserMapper;
import team03.monew.repository.user.UserRepository;
import team03.monew.util.exception.user.InvalidException;
import team03.monew.util.exception.user.UserNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserDto login(UserLoginRequest request) {
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> UserNotFoundException.withEmail(request.email()));

    if (!user.getPassword().equals(request.password())) {
      throw InvalidException.wrongPassword();
    }
    return userMapper.toDto(user);
  }
}
