package co.com.crediya.authentication.api;

import co.com.crediya.authentication.usecase.login.exception.InvalidCredentialsException;
import co.com.crediya.authentication.usecase.user.exception.DuplicateEmailException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class ErrorHandler {

    public Mono<ServerResponse> handle(Throwable ex) {
        if (ex instanceof ConstraintViolationException constraintViolationException) {
            return handleConstraintViolationException(constraintViolationException);
        } else if (ex instanceof DuplicateEmailException duplicateEmailException) {
            return handleDuplicateEmailException(duplicateEmailException);
        } else if (ex instanceof InvalidCredentialsException invalidCredentialsException) {
            return handleInvalidCredentialsException(invalidCredentialsException);
        }else {
            return handleException(ex);
        }
    }

    private Mono<ServerResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        List<Map<String, String>> violations = ex.getConstraintViolations().stream()
                .map(violation -> Map.of("field", violation.getPropertyPath().toString(), "message", violation.getMessage()))
                .toList();
        return ServerResponse.badRequest().bodyValue(Map.of(
                "error", "Invalid data",
                "violations", violations
        ));
    }

    private Mono<ServerResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        return ServerResponse.badRequest().bodyValue(Map.of(
                "error", "Email error",
                "violations", ex.getMessage()
        ));
    }

    private Mono<ServerResponse> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return ServerResponse.badRequest().bodyValue(Map.of(
                "error", "Credentials error",
                "violations", ex.getMessage()
        ));
    }

    private Mono<ServerResponse> handleException(Throwable ex) {
        return ServerResponse.badRequest().bodyValue(Map.of(
                "error", "Internal error",
                "violations", ex.getMessage()
        ));
    }

}

