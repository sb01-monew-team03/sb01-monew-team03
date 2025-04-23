package team03.monew.service.user.impl;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.dto.user.UserDto;
import team03.monew.dto.user.UserRegisterRequest;
import team03.monew.dto.user.UserUpdateRequest;
import team03.monew.entity.user.User;
import team03.monew.entity.user.User.Role;
import team03.monew.mapper.user.UserMapper;
import team03.monew.repository.user.UserRepository;
import team03.monew.service.user.UserService;
import team03.monew.util.exception.user.UserAlreadyExistsException;
import team03.monew.util.exception.user.UserNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  // 1. 사용자 등록(회원 가입)
  @Override
  public UserDto create(UserRegisterRequest request) {
    log.debug("사용자 생성 시작: request={}", request);
    // 이메일 중복 검사
    if (userRepository.existsByEmail(request.email())) {
      throw UserAlreadyExistsException.withEmail(request.email());
    }

    // 사용자 도메인 생성
    User user = new User(request.nickname(), request.email(), request.password(), Role.USER);

    // 사용자 DB 저장
    User createdUser = userRepository.save(user);
    log.info("사용자 생성 완료: userId={}, email={}", createdUser.getId(), createdUser.getEmail());
    return userMapper.toDto(createdUser);
  }

  // 2. 모든 사용자 정보 조회
  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    log.debug("모든 사용자 조회 시작");
     List<UserDto> userDtoList = userRepository.findAll().stream()
         .map(userMapper::toDto)
         .toList();
     log.info("모든 사용자 조회 완료: 사용자 수={}", userDtoList.size());
    return userDtoList;
  }

  // 3. 특정 사용자 정보 조회 - UserDto
  @Override
  @Transactional(readOnly = true)
  public UserDto findUserDtoById(UUID id) {
    log.debug("사용자 조회 시작: userId={}", id);
    UserDto userDto = userRepository.findById(id)
        .map(userMapper::toDto)
        .orElseThrow(() -> UserNotFoundException.withId(id));
    log.info("사용자 조회 완료: email={}, nickname={}", userDto.email(), userDto.nickname());
    return userDto;
  }

  // 3.1 특정 사용자 정보 조회 - User
  @Override
  @Transactional(readOnly = true)
  public User findUserById(UUID id) {
    log.debug("사용자 조회 시작: userId={}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> UserNotFoundException.withId(id));
    log.info("사용자 조회 완료: email={}, nickname={}",user.getEmail(), user.getNickname());
    return user;
  }

  // 4. 사용자 정보 수정
  @Override
  public UserDto update(UUID id, UserUpdateRequest request) {
    log.debug("사용자 수정 시작: request={}", request);
    User user = userRepository.findById(id)
        .orElseThrow(() -> UserNotFoundException.withId(id));
    user.update(request.nickname());
    log.info("사용자 수정 완료: nickname={}", user.getNickname());
    return userMapper.toDto(user);
  }

  // 5. 사용자 논리 삭제
  @Override
  public void softDelete(UUID id) {
    log.debug("사용자 논리 삭제 시작: userId={}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> UserNotFoundException.withId(id));
    user.delete();
    log.info("사용자 논리 삭제 완료: userId={}", id);
  }

  // 6. 사용자 물리 삭제
  @Override
  public void hardDelete(UUID id) {
    log.debug("사용자 물리 삭제 시작: userId={}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> UserNotFoundException.withId(id));
    userRepository.delete(user);
    log.info("사용자 물리 삭제 완료: userId={}", id);
  }
}
