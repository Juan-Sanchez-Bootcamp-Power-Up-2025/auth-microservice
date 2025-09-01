package co.com.crediya.authentication.security;

import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.model.user.gateways.UserGateway;
import co.com.crediya.authentication.r2dbc.UserReactiveRepositoryAdapter;
import co.com.crediya.authentication.security.dto.UserSecurity;
import co.com.crediya.authentication.security.jwt.provider.JwtProvider;
import co.com.crediya.authentication.security.mapper.UserSecurityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityReactiveAdapter implements UserGateway {

    private final UserReactiveRepositoryAdapter userReactiveRepositoryAdapter;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    @Override
    public Mono<User> signUp(User user) {
        user.setPassword(encodePassword(user.getPassword()));
        return userReactiveRepositoryAdapter.existsByEmail(user.getEmail())
                .flatMap(emailExists -> emailExists ?
                        Mono.error(new Throwable("Email already in use"))
                        : userReactiveRepositoryAdapter.saveUser(user));
    }

    private String encodePassword(String password) {
        System.out.println(passwordEncoder.encode(password));
        return passwordEncoder.encode(password);
    }

    @Override
    public Mono<String> login(String email, String password) {
        return userReactiveRepositoryAdapter.findByEmail(email)
                .doOnSubscribe(subscription -> log.debug("Matching password"))
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .doOnSubscribe(subscription -> log.debug("Generating token"))
                .map(user -> jwtProvider.generateToken(UserSecurityMapper.toDomain(user)))
                .switchIfEmpty(Mono.error(new Throwable("Bad credentials")));
    }

}
