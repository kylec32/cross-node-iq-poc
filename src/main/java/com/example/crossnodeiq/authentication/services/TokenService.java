package com.example.crossnodeiq.authentication.services;

import com.example.crossnodeiq.authentication.models.User;
import com.example.crossnodeiq.authentication.models.UserPrincipal;

public interface TokenService {
    String generateToken(User user);

    UserPrincipal parseToken(String token);
}
