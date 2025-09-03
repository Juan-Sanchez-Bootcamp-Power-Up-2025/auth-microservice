package co.com.crediya.authentication.security;

import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.model.user.gateways.UserGateway;
import co.com.crediya.authentication.security.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityReactiveAdapter implements UserGateway {

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    @Override
    public Mono<String> login(User user) {
        log.debug("Generating authentication token");
        return Mono.just(jwtProvider.generateToken(user));
    }

    @Override
    public boolean passwordMatches(String rawPassword, String password) {
        log.debug("Comparing passwords");
        return passwordEncoder.matches(rawPassword, password);
    }

}
