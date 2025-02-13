package org.example.services.interfaces;

import org.example.data.model.AuthOtp;
import org.example.dto.request.VerifyOtpRequest;
import org.example.dto.response.GenerateOtpResponse;
import org.example.dto.response.VerifyOtpResponseDto;

public interface AuthOtpService {

    void saveOtp(AuthOtp authOtp);

    GenerateOtpResponse generateOtp();

    VerifyOtpResponseDto verifyOtp(VerifyOtpRequest verifyOtpRequest);
}
