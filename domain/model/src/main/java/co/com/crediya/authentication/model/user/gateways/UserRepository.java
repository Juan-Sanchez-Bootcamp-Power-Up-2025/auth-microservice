package co.com.crediya.authentication.model.user.gateways;

import co.com.crediya.authentication.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> saveUser(User user);

    Mono<Boolean> existsByEmail(String email);

}
