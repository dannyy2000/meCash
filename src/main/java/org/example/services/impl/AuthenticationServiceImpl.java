package org.example.services.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.mail.MailService;
import org.example.config.validator.EmailValidatorObj;
import org.example.data.model.AuthOtp;
import org.example.data.model.User;
import org.example.data.model.enums.Role;
import org.example.data.model.enums.Status;
import org.example.data.repositories.AuthOtpRepository;
import org.example.data.repositories.UserRepository;
import org.example.dto.request.EmailNotificationRequest;
import org.example.dto.request.LoginRequest;
import org.example.dto.request.SignUpRequest;
import org.example.dto.request.VerifyUserRequest;
import org.example.dto.response.GenerateOtpResponse;
import org.example.dto.response.LoginResponseDto;
import org.example.excepions.*;
import org.example.general.ApiResponse;
import org.example.mapper.UserMapper;
import org.example.security.config.JwtProvider;
import org.example.security.config.UserPrincipal;
import org.example.services.interfaces.AuthOtpService;
import org.example.services.interfaces.AuthenticationService;
import org.example.services.interfaces.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.example.general.Message.*;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository appUserRepository;
    private final AuthOtpRepository authOtpRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthOtpService authOtpService;
    private final UserMapper userMapper;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    public static UserPrincipal userPrincipal;

    @Override
    public ApiResponse<?> createAccount(SignUpRequest signUpRequest) {
        try {
            Optional<User> findUser = appUserRepository.findUserByEmail(signUpRequest.getEmail());

            if (findUser.isPresent()) throw new UserExistException(EMAIL_FOUND);

            User appUser = userMapper.signUpRequestToUser(signUpRequest);
            appUser.validateUser();
            String accountNumber = generateAccountNumber();
            appUser.setAccountNumber(accountNumber);
            appUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            appUser.setCreatedAt(LocalDateTime.now());
            appUser.setRole(Role.USER);
            log.info("this is the details{}",appUser.getLastName());
            appUserRepository.save(appUser);
            log.info("After kinin{}",appUser.getFirstName());

            GenerateOtpResponse otpResponse = authOtpService.generateOtp();
            String otp = otpResponse.getOtp();
            log.info("This is ur opt{}",otp);

            EmailNotificationRequest emailNotificationRequest = new EmailNotificationRequest();
            emailNotificationRequest.setRecipients(signUpRequest.getEmail());
            emailNotificationRequest.setText("Your otp is " + otp);
            log.info("Before sending email to: {}", signUpRequest.getEmail());
            mailService.sendMail(emailNotificationRequest);
            log.info("Email sent successfully!");

            return ApiResponse.builder()
                    .message("Thanks for signing up. Kindly check your email to activate your account")
                    .status(Status.SUCCESS)
                    .data(appUser)
                    .build();
        } catch (UserExistException | IllegalArgumentException e) {
            return ApiResponse.builder()
                    .message(e.getLocalizedMessage())
                    .status(Status.BAD_REQUEST)
                    .build();
        }
    }

    @Override
    public ApiResponse<?> login(LoginRequest loginRequest) {
        try {

            Optional<User> user = appUserRepository.findUserByEmail(loginRequest.getEmail());
            log.info("This is the user {}",user.get().getEmail());

            if (!user.get().isEnabled()) {
                throw new UserNotEnabledException("User is not enabled");
            }
            log.info("i is here.....1");

            boolean isPassword = passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword());
            if (!isPassword) {
                throw new InvalidPasswordException("Password does not match");
            }
            log.info("i is here.....2");

            Authentication authentication = authenticationManager.authenticate(new
                    UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));

            log.info("i is here.....3");
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security kinikankinikan {}",SecurityContextHolder.getContext().getAuthentication());

            userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("this is the user principal{}",userPrincipal.getEmail());


            String jwt = jwtProvider.generateToken(user.get());
            log.info("This is the jwt{}",jwt);

            return new LoginResponseDto(LOGIN_SUCCESS,
                    Status.SUCCESS,
                    jwt, refreshTokenService.generateRefreshToken(user.get().getId()).getToken());
        } catch (UserNotEnabledException | InvalidPasswordException | AuthenticationException e) {
            return new LoginResponseDto(e.getLocalizedMessage(), Status.BAD_REQUEST);
        }
    }

    @Override
    public ApiResponse<?> verifyUser(VerifyUserRequest verifyUserRequest) {
        try {

            Optional<User> optionalAppUser = appUserRepository.findUserByEmail(verifyUserRequest.getEmail());

            if (optionalAppUser.isEmpty()) {
                throw new UserNotFoundException(USER_NOT_FOUND);
            }

            Optional<AuthOtp> otpOptional = authOtpRepository.findByOtpValue(verifyUserRequest.getOtp());

            if (otpOptional.isEmpty() || otpOptional.get().isExpired() || otpOptional.get().isUsed()) {
                throw new OtpValidationException("Invalid Otp or Otp has been used");
            }

            optionalAppUser.get().setEnabled(true);
            appUserRepository.save(optionalAppUser.get());

            otpOptional.get().setUsed(true);
            authOtpRepository.save(otpOptional.get());

            return ApiResponse.builder()
                    .message(EMAIL_VERIFIED)
                    .status(Status.SUCCESS)
                    .build();
        } catch (UserNotFoundException | OtpValidationException e) {
            return ApiResponse.builder()
                    .message("Something went wrong" + e.getLocalizedMessage())
                    .status(Status.BAD_REQUEST)
                    .build();
        }
    }


    private String generateAccountNumber () {
        return String.valueOf(UUID.randomUUID().getLeastSignificantBits()).substring(1, 11);
    }
}
