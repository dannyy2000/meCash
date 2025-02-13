package org.example.services.interfaces;

import lombok.extern.slf4j.Slf4j;
import org.example.data.model.RefreshToken;
import org.example.data.model.User;
import org.example.data.model.enums.AccountType;
import org.example.data.model.enums.Status;
import org.example.data.repositories.UserRepository;
import org.example.dto.request.LoginRequest;
import org.example.dto.request.SignUpRequest;
import org.example.dto.response.GenerateOtpResponse;
import org.example.dto.response.LoginResponseDto;
import org.example.general.ApiResponse;
import org.example.general.ErrorMessages;
import org.example.mapper.UserMapper;
import org.example.security.config.JwtProvider;
import org.example.security.config.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.example.general.ErrorMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Slf4j
public class AuthenticationServiceTest {

    @MockBean
    private UserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    @Mock
    private AuthOtpService authOtpService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserMapper userMapper;

    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;


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

    }

    @Test
    void testCreateAccount_Success() {
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .accountType(signUpRequest.getAccountType())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .AccountNumber("1234678900")
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
                .AccountNumber("1234678900")
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
                .AccountNumber("1234678900")
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
                .AccountNumber("1234678900")
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
                .AccountNumber("1234678900")
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
                .AccountNumber("1234678900")
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
                .AccountNumber("1234678900")
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
                .AccountNumber("1234678900")
                .build();


        ApiResponse<?> response = authenticationService.createAccount(signUpRequest);
        when(userMapper.signUpRequestToUser(signUpRequest)).thenReturn(user);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        log.info(response.getMessage());
        assertTrue(response.getMessage().contains(CANNOT_BE_EMPTY_OR_NULL));

        verify(appUserRepository, never()).save(any());
    }

//    @Test
//    void testLogin_Success() {
//        // Use a valid encoded password
//        String encodedPassword = "$2a$10$7Qm9HfX7iJ8V2FqY8WKPFOqbsZ/jN6cfp8xjHlhgMbvIcPr.T6Pa2"; // Example valid bcrypt hash
//
//        // Mock user repository to return the user with a hashed password
//        User user = User.builder()
//                .email(loginRequest.getEmail())
//                .password(encodedPassword) // ✅ Store encoded password
//                .isEnabled(true)
//                .build();
//        when(appUserRepository.save(any())).thenReturn(user);
//        when(appUserRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
//
//        // ✅ Ensure password matches properly
//        when(passwordEncoder.matches(loginRequest.getPassword(), encodedPassword)).thenReturn(true);
//
//        // Properly mock the authentication process
//        Authentication authentication = mock(Authentication.class);
//        when(authenticationManager.authenticate(any())).thenReturn(authentication);
//
//        // Ensure authentication returns a valid UserPrincipal
//        UserPrincipal userPrincipal = mock(UserPrincipal.class);
//        when(authentication.getPrincipal()).thenReturn(userPrincipal);
//        when(userPrincipal.getEmail()).thenReturn(user.getEmail());
//
//        // Mock JWT and refresh token generation
//        when(jwtProvider.generateToken(user)).thenReturn("mocked-jwt-token");
//
//        // Execute login
//        ApiResponse<?> response = authenticationService.login(loginRequest);
//
//        // Assertions
//        assertNotNull(response);
//        assertEquals(Status.SUCCESS, response.getStatus());
//        assertInstanceOf(LoginResponseDto.class, response);
//
//        LoginResponseDto loginResponse = (LoginResponseDto) response;
//        assertEquals("mocked-jwt-token", loginResponse.getToken());
//
//        // Verify interactions
//        verify(authenticationManager).authenticate(any());
//        verify(jwtProvider).generateToken(user);
//
//    }
//

}
