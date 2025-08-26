package co.com.crediya.authentication.api;

import co.com.crediya.authentication.api.dto.UserRequestDto;
import co.com.crediya.authentication.api.mapper.UserMapper;
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

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;

    private final ErrorHandler errorHandler;

    private final Validator validator;

    private final TransactionalOperator tx;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDto.class)
                .doOnSubscribe(subscription -> log.debug(">> POST /api/v1/users - start"))
                .flatMap(dto -> {
                            Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);
                            return violations.isEmpty() ? Mono.just(dto) : Mono.error(new ConstraintViolationException(violations));
                        })
                .map(UserMapper::toDomain)
                .flatMap(user -> tx.transactional(
                        userUseCase.saveUser(user)
                                .doOnSuccess(userSuccess -> log.info("User registered in the database"))
                                .doOnError(error -> log.error("User registration failed: {}", error.getMessage()))
                ))
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser))
                .onErrorResume(errorHandler::handle)
                .doFinally(signalType -> log.debug("<< POST /api/v1/users - end"));
    }

}
