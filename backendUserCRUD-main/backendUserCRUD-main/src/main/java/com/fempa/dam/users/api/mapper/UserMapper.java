package com.fempa.dam.users.api.mapper;

import com.fempa.dam.users.api.dto.response.UserResponseDto;
import com.fempa.dam.users.api.entity.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

	UserResponseDto toUserResponse(UserEntity user);
}
