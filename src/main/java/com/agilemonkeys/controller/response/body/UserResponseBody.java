package com.agilemonkeys.controller.response.body;

import com.agilemonkeys.domain.Role;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponseBody {

    String username;
    String name;
    String surname;
    Role role;
}
