package team03.monew.service.user;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.dto.user.UserDto;
import team03.monew.dto.user.UserRegisterRequest;
import team03.monew.entity.user.User;
import team03.monew.mapper.user.UserMapper;
import team03.monew.repository.user.UserRepository;
import team03.monew.util.exception.user.UserAlreadyExistsException;
import team03.monew.util.exception.user.UserNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  // 1. 사용자 등록(회원 가입)
  public UserDto create(UserRegisterRequest request) {
    // 이메일 중복 검사
    if (userRepository.existsByEmail(request.email())) {
      throw UserAlreadyExistsException.withEmail(request.email());
    }

    // 사용자 도메인 생성
    User user = new User(request.nickname(), request.email(), request.password());

    // 사용자 DB 저장
    User createdUser = userRepository.save(user);
    return userMapper.toDto(createdUser);
  }

  // 2. 모든 사용자 정보 조회
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(userMapper::toDto)
        .toList();
  }

  // 3. 특정 사용자 정보 조회
  @Transactional(readOnly = true)
  public UserDto findById(UUID id) {
    return userRepository.findById(id)
        .map(userMapper::toDto)
        .orElseThrow(() -> UserNotFoundException.withId(id));
  }

  // 4. 사용자 정보 수정
  public UserDto update(UUID id, String newNickname) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> UserNotFoundException.withId(id));
    user.update(newNickname);

    return userMapper.toDto(user);
  }

  // 5. 사용자 논리 삭제
  public void softDelete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> UserNotFoundException.withId(id));
    user.delete();
  }

  // 6. 사용자 물리 삭제
  public void hardDelete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> UserNotFoundException.withId(id));
    userRepository.delete(user);
  }
}
