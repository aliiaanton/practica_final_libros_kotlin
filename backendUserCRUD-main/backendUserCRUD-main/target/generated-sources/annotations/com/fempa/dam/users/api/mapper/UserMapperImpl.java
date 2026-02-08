package com.fempa.dam.users.api.mapper;

import com.fempa.dam.users.api.dto.response.UserResponseDto;
import com.fempa.dam.users.api.entity.user.UserEntity;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-08T17:13:48+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDto toUserResponse(UserEntity user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDto.UserResponseDtoBuilder userResponseDto = UserResponseDto.builder();

        userResponseDto.id( user.getId() );
        userResponseDto.name( user.getName() );
        userResponseDto.email( user.getEmail() );
        userResponseDto.role( user.getRole() );

        return userResponseDto.build();
    }
}
