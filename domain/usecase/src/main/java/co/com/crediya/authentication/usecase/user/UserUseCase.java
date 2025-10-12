package co.com.crediya.authentication.usecase.user;

import co.com.crediya.authentication.usecase.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.model.user.gateways.UserRepository;
import co.com.crediya.authentication.usecase.user.exception.DuplicateEmailException;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> saveUser(User user) {
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(emailExists -> emailExists ? Mono.error(new DuplicateEmailException(user.getEmail()))
                        : userRepository.saveUser(user));
    }

    public Mono<User> findByEmailAndDocumentId(String email, String documentId) {
        return userRepository.findByEmailAndDocumentId(email, documentId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(email, documentId)));
    }

    public Mono<List<String>> findAdminEmails() {
        return userRepository.findAdminEmails()
                .map(String::trim)
                .filter(email -> !email.isBlank())
                .distinct()
                .collectList();
    }

}
