package co.com.crediya.authentication.api;

import co.com.crediya.authentication.api.dto.LoginDto;
import co.com.crediya.authentication.api.dto.UserRequestDto;
import co.com.crediya.authentication.api.mapper.UserMapper;
import co.com.crediya.authentication.usecase.login.LoginUseCase;
import co.com.crediya.authentication.usecase.login.SignUpUseCase;
import co.com.crediya.authentication.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

    private final SignUpUseCase signUpUseCase;

    private final ErrorHandler errorHandler;

    private final Validator validator;

    private final TransactionalOperator transactionalOperator;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDto.class)
                .doOnSubscribe(subscription -> log.debug(">> POST /api/v1/users - start"))
                .flatMap(dto -> {
                            Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);
                            return violations.isEmpty() ? Mono.just(dto) : Mono.error(new ConstraintViolationException(violations));
                        })
                .map(UserMapper::toDomain)
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

    public Mono<ServerResponse> listenExistsUserByEmailAndDocumentId(ServerRequest serverRequest) {
        Optional<String> email = serverRequest.queryParam("email");
        Optional<String> documentId = serverRequest.queryParam("documentId");

        return userUseCase.existsByEmailAndDocumentId(email.get(), documentId.get())
                .doOnSubscribe(subscription -> log.debug(">> GET /api/v1/users/validate - start"))
                .flatMap(valid -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("valid", valid)))
                .as(transactionalOperator::transactional)
                .doOnSuccess(success -> log.info("Validation query successful"))
                .doOnError(error -> log.error("Validation query failed: {}", error.getMessage()))
                .doFinally(signalType -> log.debug("<< POST /api/v1/users/validate - end"));
    }

    public Mono<ServerResponse> listenSignUp(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDto.class)
                .doOnSubscribe(subscription -> log.debug(">> POST /api/v1/signup - start"))
                .flatMap(dto -> {
                    Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);
                    return violations.isEmpty() ? Mono.just(dto) : Mono.error(new ConstraintViolationException(violations));
                })
                .map(UserMapper::toDomain)
                .flatMap(signUpUseCase::singUp)
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser))
                .onErrorResume(errorHandler::handle)
                .doFinally(signalType -> log.debug("<< POST /api/v1/signup - end"));
    }

    public Mono<ServerResponse> listenLogin(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginDto.class)
                .doOnSubscribe(subscription -> log.debug(">> POST /api/v1/login - start"))
                .flatMap(loginDto -> loginUseCase.login(loginDto.email(), loginDto.password()))
                .flatMap(token -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("token", token)))
                .onErrorResume(errorHandler::handle)
                .doFinally(signalType -> log.debug("<< POST /api/v1/login - end"));
    }

}
