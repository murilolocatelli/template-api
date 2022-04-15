package com.example.template.api.mapper;

import com.example.template.api.dto.UserDto;
import com.example.template.api.model.User;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    User convertUserDtoToUser(UserDto userDto);

    UserDto convertUserToUserDto(User user);

}
