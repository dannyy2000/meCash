package org.example.services.interfaces;

import lombok.extern.slf4j.Slf4j;
import org.example.data.model.AuthOtp;
import org.example.data.model.User;
import org.example.data.model.Wallet;
import org.example.data.model.enums.AccountType;
import org.example.data.model.enums.Status;
import org.example.data.repositories.AuthOtpRepository;
import org.example.data.repositories.UserRepository;
import org.example.data.repositories.WalletRepository;
import org.example.dto.request.LoginRequest;
import org.example.dto.request.SignUpRequest;
import org.example.dto.request.VerifyUserRequest;
import org.example.dto.response.GenerateOtpResponse;
import org.example.general.ApiResponse;
import org.example.general.ErrorMessages;
import org.example.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.example.general.ErrorMessages.*;
import static org.example.general.Message.EMAIL_VERIFIED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Slf4j
public class AuthenticationServiceTest {

    @MockBean
    private UserRepository appUserRepository;

    @MockBean
    private WalletRepository walletRepository;

    @MockBean
    private AuthOtpRepository authOtpRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    @Mock
    private AuthOtpService authOtpService;

    @Mock
    private UserMapper userMapper;

    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;
    private VerifyUserRequest verifyUserRequest;


    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setAccountType(AccountType.USD);
        signUpRequest.setFirstName("Daniel");
        signUpRequest.setLastName("Akins");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        verifyUserRequest = new VerifyUserRequest();
        verifyUserRequest.setEmail("test@example.com");
        verifyUserRequest.setOtp("234567");

    }

    @Test
    void testCreateAccount_Success() {
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .accountType(signUpRequest.getAccountType())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .accountNumber("1234678900")
                .build();
        when(appUserRepository.findUserByEmail(signUpRequest.getEmail())).thenReturn(Optional.empty());
        when(userMapper.signUpRequestToUser(signUpRequest)).thenReturn(user);
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");
        when(authOtpService.generateOtp()).thenReturn(new GenerateOtpResponse(1L, "234567"));
        when(appUserRepository.save(any())).thenReturn(user);
        ApiResponse<?> response = authenticationService.createAccount(signUpRequest);

        assertNotNull(response);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals("Thanks for signing up. Kindly check your email to activate your account", response.getMessage());
    }

    @Test
    void testCannotAccountWhenUserAlreadyExists() {
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .accountType(signUpRequest.getAccountType())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .accountNumber("1234678900")
                .build();
        when(appUserRepository.findUserByEmail(signUpRequest.getEmail())).thenReturn(Optional.of(user));

        ApiResponse<?> response = authenticationService.createAccount(signUpRequest);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        assertEquals("User already exist", response.getMessage());

        verify(appUserRepository, never()).save(any());
    }

    @Test
    void testCannotAccountWithInvalidEmailFormat() {
        signUpRequest.setEmail("dan");
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .accountType(signUpRequest.getAccountType())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .accountNumber("1234678900")
                .build();


        ApiResponse<?> response = authenticationService.createAccount(signUpRequest);
        when(userMapper.signUpRequestToUser(signUpRequest)).thenReturn(user);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        log.info(response.getMessage());
        assertTrue(response.getMessage().contains(INVALID_EMAIL_ADDRESS));

        verify(appUserRepository, never()).save(any());
    }

    @Test
    void testCannotAccountWithEmptyEmail() {
        signUpRequest.setEmail("");
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .accountType(signUpRequest.getAccountType())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .accountNumber("1234678900")
                .build();


        ApiResponse<?> response = authenticationService.createAccount(signUpRequest);
        when(userMapper.signUpRequestToUser(signUpRequest)).thenReturn(user);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        log.info(response.getMessage());
        assertTrue(response.getMessage().contains(ErrorMessages.EMAIL_EMPTY));

        verify(appUserRepository, never()).save(any());
    }

    @Test
    void testCannotAccountWithInvalidFirstName() {
        signUpRequest.setFirstName("user!name");
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .accountType(signUpRequest.getAccountType())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .accountNumber("1234678900")
                .build();


        ApiResponse<?> response = authenticationService.createAccount(signUpRequest);
        when(userMapper.signUpRequestToUser(signUpRequest)).thenReturn(user);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        log.info(response.getMessage());
        assertTrue(response.getMessage().contains(INVALID_FORMAT_NAME));

        verify(appUserRepository, never()).save(any());
    }

    @Test
    void testCannotAccountWithEmptyFirstName() {
        signUpRequest.setFirstName("");
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .accountType(signUpRequest.getAccountType())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .accountNumber("1234678900")
                .build();


        ApiResponse<?> response = authenticationService.createAccount(signUpRequest);
        when(userMapper.signUpRequestToUser(signUpRequest)).thenReturn(user);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        log.info(response.getMessage());
        assertTrue(response.getMessage().contains(CANNOT_BE_EMPTY_OR_NULL));

        verify(appUserRepository, never()).save(any());
    }

    @Test
    void testCannotAccountWithInvalidLastName() {
        signUpRequest.setLastName("user!name");
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .accountType(signUpRequest.getAccountType())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .accountNumber("1234678900")
                .build();


        ApiResponse<?> response = authenticationService.createAccount(signUpRequest);
        when(userMapper.signUpRequestToUser(signUpRequest)).thenReturn(user);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        log.info(response.getMessage());
        assertTrue(response.getMessage().contains(INVALID_FORMAT_NAME));

        verify(appUserRepository, never()).save(any());
    }


    @Test
    void testCannotAccountWithEmptyLastName() {
        signUpRequest.setLastName("");
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .accountType(signUpRequest.getAccountType())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .accountNumber("1234678900")
                .build();


        ApiResponse<?> response = authenticationService.createAccount(signUpRequest);
        when(userMapper.signUpRequestToUser(signUpRequest)).thenReturn(user);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        log.info(response.getMessage());
        assertTrue(response.getMessage().contains(CANNOT_BE_EMPTY_OR_NULL));

        verify(appUserRepository, never()).save(any());
    }

    @Test
    void testVerifyUser_Success() {
        User user = User.builder()
                .email(verifyUserRequest.getEmail())
                .isEnabled(false)
                .build();

        AuthOtp otp = AuthOtp.builder()
                .otpValue(verifyUserRequest.getOtp())
                .used(false)
                .creationTime(LocalDateTime.now())
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();

        when(appUserRepository.findUserByEmail(verifyUserRequest.getEmail())).thenReturn(Optional.of(user));
        when(authOtpRepository.findByOtpValue(verifyUserRequest.getOtp())).thenReturn(Optional.of(otp));
        when(appUserRepository.save(any())).thenReturn(user);
        when(walletRepository.save(any())).thenReturn(new Wallet());

        ApiResponse<?> response = authenticationService.verifyUser(verifyUserRequest);

        assertNotNull(response);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(EMAIL_VERIFIED, response.getMessage());
        assertTrue(user.isEnabled());
    }

    @Test
    void testVerifyUser_UserNotFound() {
        when(appUserRepository.findUserByEmail(verifyUserRequest.getEmail())).thenReturn(Optional.empty());

        ApiResponse<?> response = authenticationService.verifyUser(verifyUserRequest);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        assertTrue(response.getMessage().contains(USER_NOT_FOUND));
    }

    @Test
    void testVerifyUser_OtpNotFound() {
        User user = User.builder()
                .email(verifyUserRequest.getEmail())
                .isEnabled(false)
                .build();

        when(appUserRepository.findUserByEmail(verifyUserRequest.getEmail())).thenReturn(Optional.of(user));
        when(authOtpRepository.findByOtpValue(verifyUserRequest.getOtp())).thenReturn(Optional.empty());

        ApiResponse<?> response = authenticationService.verifyUser(verifyUserRequest);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        assertTrue(response.getMessage().contains(INVALID_OTP_OR_USED));
    }

    @Test
    void testVerifyUser_OtpAlreadyUsed() {
        User user = User.builder()
                .email(verifyUserRequest.getEmail())
                .isEnabled(false)
                .build();

        AuthOtp otp = AuthOtp.builder()
                .otpValue(verifyUserRequest.getOtp())
                .used(true)
                .creationTime(LocalDateTime.now().minusMinutes(5))
                .expiryTime(LocalDateTime.now())
                .build();

        when(appUserRepository.findUserByEmail(verifyUserRequest.getEmail())).thenReturn(Optional.of(user));
        when(authOtpRepository.findByOtpValue(verifyUserRequest.getOtp())).thenReturn(Optional.of(otp));

        ApiResponse<?> response = authenticationService.verifyUser(verifyUserRequest);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        assertTrue(response.getMessage().contains(INVALID_OTP_OR_USED));
    }
    @Test
    void testLogin_UserNotEnabled() {
        User user = User.builder()
                .email(loginRequest.getEmail())
                .password(passwordEncoder.encode(loginRequest.getPassword()))
                .isEnabled(false)
                .build();

        when(appUserRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));

        ApiResponse<?> response = authenticationService.login(loginRequest);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        assertEquals(USER_NOT_ENABLED, response.getMessage());
    }

    @Test
    void testLogin_InvalidPassword() {
        User user = User.builder()
                .email(loginRequest.getEmail())
                .password(passwordEncoder.encode("password123"))
                .isEnabled(true)
                .build();

        when(appUserRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        ApiResponse<?> response = authenticationService.login(loginRequest);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        assertEquals(PASSWORD_MISMATCH, response.getMessage());
    }

}
