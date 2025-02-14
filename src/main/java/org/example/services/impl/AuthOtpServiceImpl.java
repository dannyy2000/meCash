package org.example.services.impl;

import lombok.AllArgsConstructor;
import org.example.data.model.AuthOtp;
import org.example.data.model.enums.Status;
import org.example.data.repositories.AuthOtpRepository;
import org.example.dto.request.VerifyOtpRequest;
import org.example.dto.response.GenerateOtpResponse;
import org.example.dto.response.VerifyOtpResponseDto;
import org.example.excepions.OtpGenerationException;
import org.example.excepions.OtpNotFoundException;
import org.example.excepions.OtpValidationException;
import org.example.general.Message;
import org.example.services.interfaces.AuthOtpService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static org.example.general.Message.*;

@Service
@AllArgsConstructor
public class AuthOtpServiceImpl implements AuthOtpService {

    private final AuthOtpRepository authOtpRepository;

    @Override
    public void saveOtp(AuthOtp authOtp) {
        authOtpRepository.save(authOtp);
    }

    @Override
    public GenerateOtpResponse generateOtp() {
        try{
            String otpGenerated = generateRandomDigits();
            AuthOtp otp = new AuthOtp();
            otp.setOtpValue(otpGenerated);
            otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
            saveOtp(otp);
            return new GenerateOtpResponse(OTP_GENERATED_SUCCESSFULLY, Status.SUCCESS, otp.getId(), otp.getOtpValue());
        } catch (OtpGenerationException e) {
        return new GenerateOtpResponse(e.getMessage(), Status.BAD_REQUEST, null, null);
    }
    }

    @Override
    public VerifyOtpResponseDto verifyOtp(VerifyOtpRequest verifyOtpRequest) {

            try {

                boolean verified = checkIfOtpExists(verifyOtpRequest.getOtp());

                if (!verified) {
                    throw new OtpValidationException(AUTH_OTP_NOT_VERIFIED);
                }

                Optional<AuthOtp> authOtp = authOtpRepository.findByOtpValue(verifyOtpRequest.getOtp());

                if (authOtp.isEmpty()) {
                    throw new OtpNotFoundException(AUTH_OTP_NOT_FOUND);
                }

                AuthOtp otp = authOtp.get();

                if (otp.isExpired()) {
                    throw new OtpValidationException(AUTH_OTP_EXPIRED);
                }

                if (otp.isUsed()) {
                    throw new OtpValidationException(AUTH_OTP_USED);
                }

                return new VerifyOtpResponseDto(
                        OTP_VERIFIED,
                        Status.SUCCESS,
                        otp.getOtpValue()
                );

            }catch (OtpValidationException | NullPointerException e){
                return new VerifyOtpResponseDto(
                        e.getLocalizedMessage(),
                        Status.INTERNAL_SERVER_ERROR,
                        null
                );
            }
    }



    private String generateRandomDigits() throws OtpGenerationException{

        Random random = new Random();
        int number =  random.nextInt(999999);
        String otp = String.format("%06d",number);

        if(otp.isEmpty() || otp.trim().isEmpty()){
            generateRandomDigits();
        }

        if(checkIfOtpExists(otp)){
            generateRandomDigits();
        }
        return otp;

    }

    private boolean checkIfOtpExists(String otp) {
        if(otp.trim().isEmpty() || otp.isEmpty()){
            throw new OtpGenerationException(AUTH_OTP_NOT_GENERATED);
        }
        Optional<AuthOtp> newOtp = authOtpRepository.findByOtpValue(otp);
        return newOtp.isPresent();
    }
}
