package team03.monew.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import team03.monew.entity.user.User;
import team03.monew.repository.user.UserRepository;

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
  public void run(ApplicationArguments args) throws Exception {
    if (userRepository.findByEmail(adminEmail).isEmpty()) {
      User admin = new User(adminNickname, adminEmail, adminPassword, User.Role.ADMIN);
      userRepository.save(admin);
      System.out.println("관리자 계정이 생성되었습니다.");
    } else {
      System.out.println("관리자 계정이 이미 존재합니다.");
    }
  }
}
