package co.com.crediya.authentication.api;

import co.com.crediya.authentication.api.validation.ValidationException;
import co.com.crediya.authentication.usecase.user.exception.DuplicateEmailException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
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
        } else {
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

    private Mono<ServerResponse> handleException(Throwable ex) {
        return ServerResponse.badRequest().bodyValue(Map.of(
                "error", "Internal error",
                "violations", ex.getMessage()
        ));
    }

//    private record ErrorResponse(String message, Map<String, Object> details) {}
//
//    @org.springframework.web.bind.annotation.ExceptionHandler(ValidationException.class)
//    public Mono<Void> handleValidation(ValidationException ex, ServerWebExchange exchange) {
//        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
//        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
//        var body = new ErrorResponse("Invalid data", Map.of("errors", ex.getErrors()));
//        return write(exchange, body);
//    }
//
//    @org.springframework.web.bind.annotation.ExceptionHandler(DuplicateEmailException.class)
//    public Mono<Void> handleDuplicateEmail(DuplicateEmailException ex, ServerWebExchange exchange) {
//        exchange.getResponse().setStatusCode(HttpStatus.CONFLICT);
//        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
//        var body = new ErrorResponse("Email error", Map.of("errors", ex.getMessage()));
//        return write(exchange, body);
//    }
//
//    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
//    public Mono<Void> handleGeneric(Exception ex, ServerWebExchange exchange) {
//        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
//        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
//        var body = new ErrorResponse("Internal Error", Map.of());
//        return write(exchange, body);
//    }
//
//    private Mono<Void> write(ServerWebExchange exchange, Object body) {
//        var bufferFactory = exchange.getResponse().bufferFactory();
//        try {
//            var json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsBytes(body);
//            var dataBuffer = bufferFactory.wrap(json);
//            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
//        } catch (Exception e) {
//            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
//            var dataBuffer = bufferFactory.wrap("{\"message\":\"Error serializando respuesta\"}".getBytes());
//            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
//        }
//    }
}

