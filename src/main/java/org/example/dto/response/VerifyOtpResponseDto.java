package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.data.model.enums.Status;
import org.example.general.ApiResponse;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpResponseDto extends ApiResponse {

    private String otp;

    public VerifyOtpResponseDto(String message, Status status, String otp) {
        super(message, status);
        this.otp = otp;
    }
}
