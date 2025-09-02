package co.com.crediya.authentication.usecase.login;

import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.model.user.gateways.UserGateway;
import co.com.crediya.authentication.model.user.gateways.UserRepository;
import co.com.crediya.authentication.usecase.login.exception.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Mono.*;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

class LoginUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private UserRepository userRepository;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        loginUseCase = new LoginUseCase(userGateway, userRepository);
    }

    private User sampleUser() {
        return User.builder()
                .name("Name")
                .lastName("Lastname")
                .email("name@email.com")
                .password("12345678")
                .roleId("ADMIN")
                .birthDate(LocalDate.of(2000,1,1))
                .address("Street 1")
                .documentId("12354678")
                .phoneNumber("123456789")
                .baseSalary(new BigDecimal("120000"))
                .build();
    }

    @Test
    void shouldFailWhenInvalidCredentials() {
        User user = sampleUser();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(sampleUser()));
        when(userGateway.passwordMatches(user.getPassword(), user.getPassword())).thenReturn(false);

        StepVerifier.create(loginUseCase.login(user.getEmail(), user.getPassword()))
                .expectErrorMatches(ex ->
                        ex instanceof InvalidCredentialsException && ex.getMessage().equals("Invalid credentials"))
                .verify();

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userGateway,times(1)).passwordMatches(user.getPassword(), user.getPassword());
        verify(userGateway, never()).login(user);
    }

    @Test
    void shouldLoginWhenValidCredentials() {
        User user = sampleUser();
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuLnNhbmNoZXpAY3";

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(user));
        when(userGateway.passwordMatches(user.getPassword(), user.getPassword())).thenReturn(true);
        when(userGateway.login(user)).thenReturn(Mono.just(token));

        StepVerifier.create(loginUseCase.login(user.getEmail(), user.getPassword()))
                .expectNextMatches(token::equals)
                .expectComplete()
                .verify();
    }

}
