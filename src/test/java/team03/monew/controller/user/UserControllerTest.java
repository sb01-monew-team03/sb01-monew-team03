package team03.monew.controller.user;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team03.monew.dto.user.UserDto;
import team03.monew.dto.user.UserRegisterRequest;
import team03.monew.dto.user.UserUpdateRequest;
import team03.monew.service.user.UserService;
import team03.monew.util.exception.user.UserAlreadyExistsException;
import team03.monew.util.exception.user.UserNotFoundException;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @Nested
  @DisplayName("회원 가입")
  class createUser {

    @Test
    @DisplayName("회원 가입 성공")
    void create_success() throws Exception {
      // given
      // 1. 유저 생성 리퀘스트
      UserRegisterRequest request = new UserRegisterRequest("test@gmail.com", "test", "qwer1234!");

      // 2. UserDto
      UserDto userDto = new UserDto(UUID.randomUUID(), request.email(), request.nickname(), Instant.now());

      // 3. 회원 가입
      given(userService.create(request)).willReturn(userDto);

      // when, then
      // 1. mockMvc 사용
      mockMvc.perform(post("/api/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(userDto.id().toString()))
          .andExpect(jsonPath("$.email").value(request.email()))
          .andExpect(jsonPath("$.nickname").value(request.nickname()));
    }

    @Test
    @DisplayName("잘못된 리퀘스트")
    void create_badRequest() throws Exception {
      // given
      // 1. 유저 생성 리퀘스트
      UserRegisterRequest request = new UserRegisterRequest("", "", "");

      // when, then
      // 1. mockMvc 사용
      mockMvc.perform(post("/api/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일 중복")
    void create_duplicateEmail() throws Exception {
      // given
      // 1. 유저 생성 리퀘스트
      String email = "test@gmail.com";
      UserRegisterRequest request = new UserRegisterRequest(email, "test", "qwer1234!");

      // 2. 회원 가입
      given(userService.create(request)).willThrow(UserAlreadyExistsException.withEmail(email));

      // when, then
      // 1. mockMvc 사용
      mockMvc.perform(post("/api/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isConflict());
    }
  }

  @Nested
  @DisplayName("사용자 정보 수정")
  class updateUser {

    @Test
    @DisplayName("사용자 정보 수정 성공")
    void update_success() throws Exception {
      // given
      // 1. 사용자 수정 리퀘스트
      UUID userId = UUID.randomUUID();
      UserUpdateRequest request = new UserUpdateRequest("newNickname");

      // 2. UserDto
      UserDto userDto = new UserDto(userId, "test@gmail.com", request.nickname(), Instant.now());

      // 3. 사용자 수정
      given(userService.update(userId, request)).willReturn(userDto);

      // when, then
      // 1. mockMvc 사용
      mockMvc.perform(patch("/api/users/{userId}", userId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
              .header("MoNew-Request-User-ID", userId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nickname").value(request.nickname()));
    }

    @Test
    @DisplayName("잘못된 리퀘스트")
    void update_badRequest() throws Exception {
      // given
      // 1. 사용자 수정 리퀘스트
      UUID userId = UUID.randomUUID();
      UserUpdateRequest request = new UserUpdateRequest("");

      // when, then
      // 1. mockMvc 사용
      mockMvc.perform(patch("/api/users/{userId}", userId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
              .header("MoNew-Request-User-ID", userId))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 정보 없음")
    void update_notFoundUser() throws Exception {
      // given
      // 1. 사용자 수정 리퀘스트
      UUID userId = UUID.randomUUID();
      UserUpdateRequest request = new UserUpdateRequest("newNickname");

      // 2. 사용자 수정
      given(userService.update(userId, request)).willThrow(UserNotFoundException.withId(userId));

      // when, then
      // 1. mockMvc 사용
      mockMvc.perform(patch("/api/users/{userId}", userId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
              .header("MoNew-Request-User-ID", userId))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("사용자 논리 삭제")
  class softDeleteUser {

    @Test
    @DisplayName("사용자 삭제 성공")
    void softDelete_success() throws Exception {
      // given
      // 1. UserId
      UUID userId = UUID.randomUUID();

      // 2. 유저 논리 삭제
      willDoNothing().given(userService).softDelete(userId);

      // when, then
      // 1. mockMvc 사용
      mockMvc.perform(delete("/api/users/{userId}", userId)
              .contentType(MediaType.APPLICATION_JSON)
              .header("MoNew-Request-User-ID", userId))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자 정보 없음")
    void softDelete_notFoundUser() throws Exception {
      // given
      // 1. UserId
      UUID userId = UUID.randomUUID();

      // 2. 유저 논리 삭제
      willThrow(UserNotFoundException.withId(userId)).given(userService).softDelete(userId);

      // when, then
      // 1. mockMvc 사용
      mockMvc.perform(delete("/api/users/{userId}", userId)
              .contentType(MediaType.APPLICATION_JSON)
              .header("MoNew-Request-User-ID", userId))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("사용자 물리 삭제")
  class hardDeleteUser {

    @Test
    @DisplayName("사용자 삭제 성공")
    void hardDelete_success() throws Exception {
      // given
      // 1. UserId
      UUID userId = UUID.randomUUID();

      // 2. 유저 물리 삭제
      willDoNothing().given(userService).hardDelete(userId);

      // when, then
      // 1. mockMvc 사용
      mockMvc.perform(delete("/api/users/{userId}/hard", userId)
              .contentType(MediaType.APPLICATION_JSON)
              .header("MoNew-Request-User-ID", userId))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자 정보 없음")
    void hardDelete_notFoundUser() throws Exception {
      // given
      // 1. UserId
      UUID userId = UUID.randomUUID();

      // 2. 유저 물리 삭제
      willThrow(UserNotFoundException.withId(userId)).given(userService).hardDelete(userId);

      // when, then
      // 1. mockMvc 사용
      mockMvc.perform(delete("/api/users/{userId}/hard", userId)
              .contentType(MediaType.APPLICATION_JSON)
              .header("MoNew-Request-User-ID", userId))
          .andExpect(status().isNotFound());
    }
  }
}