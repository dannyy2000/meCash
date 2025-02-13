//package org.example.services.interfaces;
//import lombok.extern.slf4j.Slf4j;
//import org.example.data.model.AuthOtp;
//import org.example.data.model.enums.Status;
//import org.example.data.repositories.AuthOtpRepository;
//import org.example.dto.request.VerifyOtpRequest;
//import org.example.dto.response.GenerateOtpResponse;
//import org.example.dto.response.VerifyOtpResponseDto;
//import org.example.general.Message;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//@SpringBootTest
//@Slf4j
//class AuthOtpServiceTest {
//    @Autowired
//    private AuthOtpService authOtpService;
//
//    @Autowired
//    private AuthOtpRepository authOtpRepository;
//
//    private VerifyOtpRequest verifyOtpRequest;
//
//
//    @BeforeEach
//    void setUp() {
//        verifyOtpRequest = new VerifyOtpRequest();
//        verifyOtpRequest.setOtp("3195599");
//    }
//
//    @Test
//    void testThatOtpCanBeGenerated(){
//        GenerateOtpResponse otpResponse = authOtpService.generateOtp();
//        assertThat(otpResponse).isNotNull();
//        assertThat(otpResponse.getStatus()).isEqualTo(Status.SUCCESS);
//    }
//
//    @Test
//    void testVerifyValidOtp() {
//        GenerateOtpResponse response = authOtpService.generateOtp();
//        String otp = response.getOtp();
//        log.info("OTP Before Update: {}", otp);
//
//        VerifyOtpRequest request = new VerifyOtpRequest(otp);
//        VerifyOtpResponseDto verifyResponse = authOtpService.verifyOtp(request);
//
//        assertEquals(Status.SUCCESS, verifyResponse.getStatus());
//        assertEquals(Message.OTP_VERIFIED, verifyResponse.getMessage());
//
//        AuthOtp usedOtp = authOtpRepository.findByOtpValue(verifyResponse.getOtp()).orElse(null);
//        assertNotNull(usedOtp);
//        assertTrue(usedOtp.isUsed());
//    }
//
//    @Test
//    void testVerifyExpiredOtp() {
//        AuthOtp expiredOtp = new AuthOtp();
//        expiredOtp.setOtpValue("123457");
//        expiredOtp.setExpiryTime(LocalDateTime.now().minusMinutes(20));
//        expiredOtp.setUsed(false);
//        authOtpRepository.save(expiredOtp);
//
//        VerifyOtpRequest request = new VerifyOtpRequest("123457");
//        VerifyOtpResponseDto verifyResponse = authOtpService.verifyOtp(request);
//
//        assertEquals(Message.AUTH_OTP_EXPIRED, verifyResponse.getMessage());
//        authOtpRepository.delete(expiredOtp);
//    }
//
//    @Test
//    void testVerifyUsedOtp() {
//        AuthOtp usedOtp = new AuthOtp();
//        usedOtp.setOtpValue("654321");
//        usedOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
//        usedOtp.setUsed(true);
//        authOtpRepository.save(usedOtp);
//
//        VerifyOtpRequest request = new VerifyOtpRequest("654321");
//        VerifyOtpResponseDto verifyResponse = authOtpService.verifyOtp(request);
//        assertEquals(Message.AUTH_OTP_USED, verifyResponse.getMessage());
//    }
//
//    @Test
//    void testVerifyInvalidOtp() {
//        VerifyOtpResponseDto verifyResponse = authOtpService.verifyOtp(verifyOtpRequest);
//        assertEquals(Message.AUTH_OTP_NOT_VERIFIED, verifyResponse.getMessage());
//    }
//}
