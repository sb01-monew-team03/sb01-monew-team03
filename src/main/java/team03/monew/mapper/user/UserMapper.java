package team03.monew.mapper.user;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import team03.monew.dto.user.UserDto;
import team03.monew.entity.user.User;

@Component
@RequiredArgsConstructor
public class UserMapper {

  public UserDto toDto(User user) {
    return new UserDto(user.getId(),
        user.getEmail(),
        user.getNickname(),
        user.getCreatedAt());
  }
}

