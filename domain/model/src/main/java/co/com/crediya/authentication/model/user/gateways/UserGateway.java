package co.com.crediya.authentication.model.user.gateways;

import co.com.crediya.authentication.model.user.User;
import reactor.core.publisher.Mono;

public interface UserGateway {

    Mono<User> signUp(User user);

    Mono<String> login(String email, String password);

}
