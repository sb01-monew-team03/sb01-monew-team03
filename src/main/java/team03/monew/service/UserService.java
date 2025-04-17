package team03.monew.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team03.monew.dto.user.UserDto;
import team03.monew.dto.user.UserRegisterRequest;
import team03.monew.mapper.UserMapper;
import team03.monew.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  // 1. 사용자 등록(회원 가입)
  public UserDto create(UserRegisterRequest request) {
    return null;
  }

  // 2. 모든 사용자 정보 조회
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return null;
  }

  // 3. 특정 사용자 정보 조회
  @Transactional(readOnly = true)
  public UserDto findById(UUID id) {
    return null;
  }

  // 4. 사용자 정보 수정
  public UserDto update(UUID id, String newNickname) {
    return null;
  }

  // 5. 사용자 논리 삭제
  public void softDelete(UUID id) {
  }

  // 6. 사용자 물리 삭제
  public void hardDelete(UUID id) {
  }
}
