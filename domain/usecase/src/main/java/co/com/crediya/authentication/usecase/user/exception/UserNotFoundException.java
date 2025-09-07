package co.com.crediya.authentication.usecase.user.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String documentId) {
        super("User with document id " + documentId + " was not found");
    }
}
