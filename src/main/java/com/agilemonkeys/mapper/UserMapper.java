package com.agilemonkeys.mapper;

import com.agilemonkeys.controller.response.body.UserResponseBody;
import com.agilemonkeys.domain.User;

public class UserMapper {

    public static UserResponseBody userToUserResponseBody(User user) {
        return UserResponseBody.builder()
                .username(user.getUsername())
                .name(user.getName())
                .surname(user.getSurname())
                .role(user.getRole())
                .build();

    }
}
