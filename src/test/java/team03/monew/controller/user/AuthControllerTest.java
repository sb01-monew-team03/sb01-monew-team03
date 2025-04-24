package team03.monew.controller.user;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team03.monew.dto.user.UserDto;
import team03.monew.dto.user.UserLoginRequest;
import team03.monew.service.user.AuthService;
import team03.monew.service.user.UserService;
import team03.monew.util.exception.user.InvalidException;
import team03.monew.util.exception.user.UserNotFoundException;
import team03.monew.util.interceptor.UserInterceptor;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private AuthService authService;

  @MockitoBean
  private UserService userService;

  @Test
  @DisplayName("로그인 성공")
  void login_success() throws Exception {
    // given
    // 1. 로그인 리퀘스트
    String email = "test@gmail.com";
    String password = "qwer1234!";
    UserLoginRequest request = new UserLoginRequest(email, password);

    // 2. 로그인을 위한 유저 dto 생성
    UserDto userDto = new UserDto(UUID.randomUUID(), email, "test", Instant.now());

    // 3. 로그인
    given(authService.login(request)).willReturn(userDto);

    // when, then
    // 1. mockMvc 사용
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userDto.id().toString()))
        .andExpect(jsonPath("$.email").value(userDto.email()))
        .andExpect(jsonPath("$.nickname").value(userDto.nickname()));
  }

  @Test
  @DisplayName("사용자가 존재하지 않음")
  void login_notFoundUser() throws Exception{
    // given
    // 1. 로그인 리퀘스트 생성
    String email = "test@gmail.com";
    String password = "qwer1234!";
    UserLoginRequest request = new UserLoginRequest(email, password);

    // 2. 로그인
    given(authService.login(request)).willThrow(UserNotFoundException.withEmail(email));

    // when, then
    // 1. mockMvc 사용
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("잘못된 비밀번호")
  void login_wrongPassword() throws Exception{
    // given
    // 1. 로그인 리퀘스트 생성
    String email = "test@gmail.com";
    String password = "qwer1234!";
    UserLoginRequest request = new UserLoginRequest(email, password);

    // 2. 로그인
    given(authService.login(request)).willThrow(InvalidException.wrongPassword());

    // when, then
    // 1. mockMvc 사용
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("리퀘스트가 잘못됨")
  void login_badRequest() throws Exception {
    // given
    // 1. 로그인 리퀘스트
    String email = "";
    String password = "";
    UserLoginRequest request = new UserLoginRequest(email, password);

    // when, then
    // 1. mockMvc 사용
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}