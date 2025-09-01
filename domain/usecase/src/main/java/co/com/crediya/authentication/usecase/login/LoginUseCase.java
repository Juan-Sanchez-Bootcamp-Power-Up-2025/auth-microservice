package co.com.crediya.authentication.usecase.login;

import co.com.crediya.authentication.model.user.gateways.UserGateway;
import co.com.crediya.authentication.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserGateway userGateway;

    private final UserRepository userRepository;

    public Mono<String> login(String email, String password) {
        return userGateway.login(email, password);
    }

}
