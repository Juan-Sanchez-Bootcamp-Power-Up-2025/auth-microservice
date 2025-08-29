package co.com.crediya.authentication.usecase.user.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("The email " + email + " is already registered for another user");
    }

}
