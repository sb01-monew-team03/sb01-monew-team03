package team03.monew.service.user;

import java.util.List;
import java.util.UUID;
import team03.monew.dto.user.UserDto;
import team03.monew.dto.user.UserRegisterRequest;
import team03.monew.dto.user.UserUpdateRequest;
import team03.monew.entity.user.User;

public interface UserService {

  // 1. 사용자 등록(회원 가입)
  UserDto create(UserRegisterRequest request);

  // 2. 모든 사용자 정보 조회
  List<UserDto> findAll();

  // 3. 특정 사용자 정보 조회 - UserDto
  UserDto findUserDtoById(UUID id);

  // 3.1 특정 사용자 정보 조회 - User
  User findUserById(UUID id);

  // 4. 사용자 정보 수정
  UserDto update(UUID id, UserUpdateRequest request);

  // 5. 사용자 논리 삭제
  void softDelete(UUID id);

  // 6. 사용자 물리 삭제
  void hardDelete(UUID id);
}
