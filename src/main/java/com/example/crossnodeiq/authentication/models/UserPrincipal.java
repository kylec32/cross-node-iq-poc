package com.example.crossnodeiq.authentication.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
public class UserPrincipal extends User{
    @Getter
    private final String token;

    public UserPrincipal(int id, String username, String token) {
        super(id, username);
        this.token = token;
    }
}
