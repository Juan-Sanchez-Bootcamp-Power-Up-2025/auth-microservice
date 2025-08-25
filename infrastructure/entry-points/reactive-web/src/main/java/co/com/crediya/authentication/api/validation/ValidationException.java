package co.com.crediya.authentication.api.validation;

import java.util.Map;

public class ValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public ValidationException(Map<String, String> errors) {
        super("Validation error");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

}
