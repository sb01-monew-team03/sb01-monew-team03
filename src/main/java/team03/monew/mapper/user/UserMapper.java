package team03.monew.mapper.user;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import team03.monew.dto.user.UserDto;
import team03.monew.entity.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserDto toDto(User user);
}

