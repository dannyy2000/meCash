package org.example.services.interfaces;

import org.example.dto.request.LoginRequest;
import org.example.dto.request.SignUpRequest;
import org.example.dto.request.VerifyUserRequest;
import org.example.general.ApiResponse;

public interface AuthenticationService {
    ApiResponse<?> createAccount(SignUpRequest signUpRequest);
    ApiResponse<?>login(LoginRequest loginRequest);
    ApiResponse<?> verifyUser(VerifyUserRequest verifyUserRequest);


}
