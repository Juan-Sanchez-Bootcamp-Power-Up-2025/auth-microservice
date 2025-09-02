package co.com.crediya.authentication.usecase.login;

import co.com.crediya.authentication.model.user.gateways.UserGateway;
import co.com.crediya.authentication.model.user.gateways.UserRepository;
import co.com.crediya.authentication.usecase.login.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserGateway userGateway;

    private final UserRepository userRepository;

    public Mono<String> login(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .flatMap(user -> !userGateway.passwordMatches(password, user.getPassword())
                        ? Mono.error(new InvalidCredentialsException())
                        : userGateway.login(user));
    }

}
