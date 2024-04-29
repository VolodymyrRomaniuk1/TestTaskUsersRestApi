package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.model.User;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    User toUser(UserDto userDto);

    UserDto toUserDto(User user);
}
