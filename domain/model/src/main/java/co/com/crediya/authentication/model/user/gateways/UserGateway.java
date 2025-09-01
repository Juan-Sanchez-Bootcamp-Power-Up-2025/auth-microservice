package co.com.crediya.authentication.model.user.gateways;

import reactor.core.publisher.Mono;

public interface UserGateway {

    Mono<String> login(String email, String password);

}
