package co.com.crediya.authentication.model.user.gateways;

import co.com.crediya.authentication.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> saveUser(User user);

    Mono<Boolean> existsByEmail(String email);

    Mono<User> findByEmailAndDocumentId(String email, String documentId);

    Mono<User> findByEmail(String email);

}
