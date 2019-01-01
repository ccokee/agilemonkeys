package com.agilemonkeys.mapper;

import com.agilemonkeys.controller.ResponseBody.UserResponseBody;
import com.agilemonkeys.domain.User;

public class UserMapper {

    public static UserResponseBody userToUserReponseBody(User user) {
        return UserResponseBody.builder()
                .username(user.getUsername())
                .name(user.getName())
                .surname(user.getSurname())
                .role(user.getRole())
                .build();

    }
}
