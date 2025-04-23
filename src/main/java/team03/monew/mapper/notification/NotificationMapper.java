package team03.monew.mapper.notification;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import team03.monew.dto.notification.NotificationDto;
import team03.monew.entity.notification.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

  @Mapping(source = "user.id", target = "userId")
  NotificationDto toDto(Notification notification);
}