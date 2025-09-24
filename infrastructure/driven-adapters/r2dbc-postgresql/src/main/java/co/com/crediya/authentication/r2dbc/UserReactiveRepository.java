package co.com.crediya.authentication.r2dbc;

import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.r2dbc.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, String>, ReactiveQueryByExampleExecutor<UserEntity> {

    Mono<Boolean> existsByEmailIgnoreCase(String email);

    Mono<User> findByEmailAndDocumentId(String email, String documentId);

    Mono<User> findByEmail(String email);

    @Query("""
            SELECT email
            FROM users
            WHERE ROLE_ID = 'ADMIN'
            """)
    Flux<String> findAdminEmails();

}
