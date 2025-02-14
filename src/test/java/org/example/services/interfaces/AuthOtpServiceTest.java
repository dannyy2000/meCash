package org.example.services.interfaces;
import lombok.extern.slf4j.Slf4j;
import org.example.data.model.AuthOtp;
import org.example.data.model.enums.Status;
import org.example.data.repositories.AuthOtpRepository;
import org.example.dto.request.VerifyOtpRequest;
import org.example.dto.response.GenerateOtpResponse;
import org.example.dto.response.VerifyOtpResponseDto;
import org.example.general.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
class AuthOtpServiceTest {
    @Autowired
    private AuthOtpService authOtpService;

    private VerifyOtpRequest verifyOtpRequest;


    @BeforeEach
    void setUp() {
        verifyOtpRequest = new VerifyOtpRequest();
        verifyOtpRequest.setOtp("3195599");
    }

    @Test
    void testThatOtpCanBeGenerated(){
        GenerateOtpResponse otpResponse = authOtpService.generateOtp();
        assertThat(otpResponse).isNotNull();
        assertThat(otpResponse.getStatus()).isEqualTo(Status.SUCCESS);
    }

    @Test
    void testVerifyValidOtp() {
        GenerateOtpResponse response = authOtpService.generateOtp();
        String otp = response.getOtp();
        log.info("OTP Before Update: {}", otp);

        VerifyOtpRequest request = new VerifyOtpRequest(otp);
        VerifyOtpResponseDto verifyResponse = authOtpService.verifyOtp(request);

        assertEquals(Status.SUCCESS, verifyResponse.getStatus());
        assertEquals(Message.OTP_VERIFIED, verifyResponse.getMessage());

    }

    @Test
    void testVerifyInvalidOtp() {
        VerifyOtpResponseDto verifyResponse = authOtpService.verifyOtp(verifyOtpRequest);
        assertEquals(Message.AUTH_OTP_NOT_VERIFIED, verifyResponse.getMessage());
    }
}
