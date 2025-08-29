package co.com.crediya.authentication.usecase.user;

import lombok.RequiredArgsConstructor;
import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.model.user.gateways.UserRepository;
import co.com.crediya.authentication.usecase.user.exception.DuplicateEmailException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> saveUser(User user) {
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(emailExists -> emailExists ? Mono.error(new DuplicateEmailException(user.getEmail()))
                        : userRepository.saveUser(user));
    }

    public Mono<Boolean> existsByEmailAndDocumentId(String email, String documentId) {
        return userRepository.existsByEmailAndDocumentId(email, documentId);
    }

}
