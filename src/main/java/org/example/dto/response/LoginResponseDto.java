package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.data.model.enums.Status;
import org.example.general.ApiResponse;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class LoginResponseDto extends ApiResponse {

    private String token;
    private String refreshToken;

    public LoginResponseDto(String message, Status status, String token, String refreshToken) {
        super(message, status);
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public LoginResponseDto(String message,Status status){
        super(message, status);
    }

}
