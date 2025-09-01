package co.com.crediya.authentication.security;

import co.com.crediya.authentication.model.user.gateways.UserGateway;
import co.com.crediya.authentication.r2dbc.UserReactiveRepositoryAdapter;
import co.com.crediya.authentication.security.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoginReactiveAdapter implements UserGateway {

    private final UserReactiveRepositoryAdapter userReactiveRepositoryAdapter;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    @Override
    public Mono<String> login(String email, String password) {
        return userReactiveRepositoryAdapter.findByEmail(email)
                .filter(user -> passwordEncoder.matches(user.getPassword(), password))
                .map(user -> jwtProvider.generateToken((UserDetails) user))
                .switchIfEmpty(Mono.error(new Throwable("Bad credentials")));
    }

}
