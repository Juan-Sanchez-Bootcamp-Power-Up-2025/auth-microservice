package co.com.crediya.authentication.r2dbc;

import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.model.user.gateways.UserRepository;
import co.com.crediya.authentication.r2dbc.entity.UserEntity;
import co.com.crediya.authentication.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        String,
        UserReactiveRepository
> implements UserRepository {

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, User.class));
    }

    @Override
    public Mono<User> saveUser(User user) {
        log.debug("Saving user in the database");
        return super.save(user);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        log.debug("Querying the database for an email address");
        return repository.existsByEmailIgnoreCase(email);
    }

    @Override
    public Mono<User> findByEmailAndDocumentId(String email, String documentId) {
        log.debug("Querying the database for user by email address document id");
        return repository.findByEmailAndDocumentId(email, documentId);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        log.debug("Querying the database for user by email address");
        return repository.findByEmail(email);
    }

}
