package org.example.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.dto.request.LoginRequest;
import org.example.dto.request.SignUpRequest;
import org.example.dto.request.VerifyUserRequest;
import org.example.general.ApiResponse;
import org.example.services.interfaces.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController extends Controller{
    private  final AuthenticationService auth;
    public AuthController(HttpServletResponse response, AuthenticationService auth) {
        super(response);
        this.auth = auth;
    }

    @PostMapping("/createAccount")
    public ApiResponse<?> createAccount(@Valid @RequestBody SignUpRequest signUpRequest){
        return responseWithUpdatedHttpStatus(auth.createAccount(signUpRequest));
    }

    @PostMapping("/signIn")
    public ApiResponse<?>signIn(@Valid @RequestBody LoginRequest loginRequest){
        return responseWithUpdatedHttpStatus(auth.login(loginRequest));
    }

    @PostMapping("/verify")
    public ApiResponse<?>verify(@Valid @RequestBody VerifyUserRequest verifyUserRequest){
        return responseWithUpdatedHttpStatus(auth.verifyUser(verifyUserRequest));
    }
}
