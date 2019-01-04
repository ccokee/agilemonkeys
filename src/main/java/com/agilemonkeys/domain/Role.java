package com.agilemonkeys.domain;

import com.agilemonkeys.exception.RoleNotValidException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ADMIN,
    USER;

    @JsonCreator
    public static Role create (String value) throws RoleNotValidException {
        if(value == null) {
            throw new RoleNotValidException("Role can't be null");
        }
        for(Role v : values()) {
            if(value.equals(v.name())) {
                return v;
            }
        }
        throw new RoleNotValidException("Role can only be ADMIN or USER.");
    }
}
