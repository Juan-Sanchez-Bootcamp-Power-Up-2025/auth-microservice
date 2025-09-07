package co.com.crediya.authentication.api;

import co.com.crediya.authentication.api.dto.LoginDto;
import co.com.crediya.authentication.api.dto.UserRequestDto;
import co.com.crediya.authentication.api.mapper.UserMapper;
import co.com.crediya.authentication.usecase.login.LoginUseCase;
import co.com.crediya.authentication.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;

    private final LoginUseCase loginUseCase;

    private final PasswordEncoder passwordEncoder;

    private final ErrorHandler errorHandler;

    private final Validator validator;

    private final TransactionalOperator transactionalOperator;

    public Mono<ServerResponse> listenLogin(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginDto.class)
                .doOnSubscribe(subscription -> log.debug(">> POST /api/v1/login - start"))
                .flatMap(dto -> {
                    Set<ConstraintViolation<LoginDto>> violations = validator.validate(dto);
                    return violations.isEmpty() ? Mono.just(dto) : Mono.error(new ConstraintViolationException(violations));
                })
                .flatMap(loginDto -> loginUseCase.login(loginDto.email(), loginDto.password()))
                .doOnSuccess(success -> log.info("Token generated successfully"))
                .doOnError(error -> log.error("Token generation failed: {}", error.getMessage()))
                .flatMap(token -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("token", token)))
                .onErrorResume(errorHandler::handle)
                .doFinally(signalType -> log.debug("<< POST /api/v1/login - end"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONSULTANT')")
    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDto.class)
                .doOnSubscribe(subscription -> log.debug(">> POST /api/v1/users - start"))
                .flatMap(dto -> {
                            Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);
                            return violations.isEmpty() ? Mono.just(dto) : Mono.error(new ConstraintViolationException(violations));
                        })
                .map(UserMapper::toDomain)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return user;
                })
                .flatMap(userUseCase::saveUser)
                .doOnSuccess(success -> log.info("User registered in the database"))
                .doOnError(error -> log.error("User registration failed: {}", error.getMessage()))
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser))
                .as(transactionalOperator::transactional)
                .onErrorResume(errorHandler::handle)
                .doFinally(signalType -> log.debug("<< POST /api/v1/users - end"));
    }

    @PreAuthorize("hasAuthority('CLIENT')")
    public Mono<ServerResponse> listenFindByDocumentId(ServerRequest serverRequest) {
        Optional<String> email = serverRequest.queryParam("email");
        Optional<String> documentId = serverRequest.queryParam("documentId");

        return userUseCase.findByEmailAndDocumentId(email.get(), documentId.get())
                .doOnSubscribe(subscription -> log.debug(">> GET /api/v1/users/validate - start"))
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .as(transactionalOperator::transactional)
                .doOnSuccess(success -> log.info("Validation query successful"))
                .doOnError(error -> log.error("Validation query failed: {}", error.getMessage()))
                .onErrorResume(errorHandler::handle)
                .doFinally(signalType -> log.debug("<< POST /api/v1/users/validate - end"));
    }

}
