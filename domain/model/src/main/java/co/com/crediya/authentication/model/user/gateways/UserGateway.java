package co.com.crediya.authentication.model.user.gateways;

import co.com.crediya.authentication.model.user.User;
import reactor.core.publisher.Mono;

public interface UserGateway {

    Mono<String> login(User user);

    boolean passwordMatches(String rawPassword, String password);

}
