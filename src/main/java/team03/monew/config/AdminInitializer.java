package team03.monew.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import team03.monew.entity.user.User;
import team03.monew.repository.user.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final UserRepository userRepository;

  @Value("${admin.email}")
  private String adminEmail;

  @Value("${admin.password}")
  private String adminPassword;

  @Value("${admin.nickname}")
  private String adminNickname;

  @Override
  public void run(ApplicationArguments args){
    if (userRepository.findByEmail(adminEmail).isEmpty()) {
      User admin = new User(adminNickname, adminEmail, adminPassword, User.Role.ADMIN);
      userRepository.save(admin);
      log.debug("관리자 계정 생성: {}", admin.getEmail());
    } else {
      log.debug("관리자 계정이 이미 있습니다.");
    }
  }
}
