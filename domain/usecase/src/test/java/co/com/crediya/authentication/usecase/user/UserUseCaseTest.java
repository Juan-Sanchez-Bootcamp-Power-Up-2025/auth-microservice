package co.com.crediya.authentication.usecase.user;

import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.model.user.gateways.UserRepository;
import co.com.crediya.authentication.usecase.user.exception.DuplicateEmailException;
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

class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private UserUseCase userUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userUseCase = new UserUseCase(userRepository);
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
    void shouldFailWhenEmailExists() {
        User user = sampleUser();
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectErrorMatches(ex ->
                        ex instanceof DuplicateEmailException && ex.getMessage().toLowerCase().contains("is already registered for another user"))
                .verify();

        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void shouldSaveWhenEmailNotExists() {
        User user = sampleUser();
        User userSaved = user.toBuilder().build();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.saveUser(user)).thenReturn(Mono.just(userSaved));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectNextMatches(out ->
                        userSaved.getName().equals(out.getName()) &&
                                userSaved.getEmail().equals(out.getEmail()))
                .expectComplete()
                .verify();

        InOrder inOrder = inOrder(userRepository);
        inOrder.verify(userRepository).existsByEmail(user.getEmail());
        inOrder.verify(userRepository).saveUser(user);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldValidateUserByEmailAndDocumentId() {
        User user = sampleUser();
        when(userRepository.existsByEmailAndDocumentId(user.getEmail(), user.getDocumentId())).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.existsByEmailAndDocumentId(user.getEmail(), user.getDocumentId()))
                .expectNextMatches(out -> out==true)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldPropagateErrorFromRepository() {
        User user = sampleUser();

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(Mono.error(new RuntimeException("DB Down")));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectErrorMessage("DB Down")
                .verify();

        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, never()).saveUser(any());
    }

}
