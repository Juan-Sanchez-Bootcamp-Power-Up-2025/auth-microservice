package co.com.crediya.authentication.usecase.login;

import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.model.user.gateways.UserGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SignUpUseCase {

    private final UserGateway userGateway;

    public Mono<User> singUp(User user) {
        return userGateway.signUp(user);
    }

}
