package team03.monew.integration.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import team03.monew.dto.user.UserDto;
import team03.monew.dto.user.UserLoginRequest;
import team03.monew.dto.user.UserRegisterRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserFlowIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  @DisplayName("회원 가입 -> 로그인 테스트")
  void createUser() {
    // 1. 회원 가입
    String email = "test@gmail.com";
    String password = "qwer1234!";
    UserRegisterRequest userRegisterRequest = new UserRegisterRequest(email, "test", password);

    ResponseEntity<UserDto> createdUser = restTemplate.postForEntity("/api/users",
        userRegisterRequest, UserDto.class);

    assertNotNull(createdUser.getBody());
    assertEquals(createdUser.getStatusCode(), HttpStatus.CREATED);

    // 2. 로그인
    UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);

    ResponseEntity<UserDto> loginUser = restTemplate.postForEntity("/api/users/login",
        userLoginRequest, UserDto.class);

    assertNotNull(loginUser.getBody());
    assertEquals(loginUser.getStatusCode(), HttpStatus.OK);

  }

}
