package co.com.crediya.authentication.r2dbc;

import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.model.user.gateways.UserRepository;
import co.com.crediya.authentication.r2dbc.entity.UserEntity;
import co.com.crediya.authentication.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

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
    @Transactional
    public Mono<User> saveUser(User user) {
        return super.save(user);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }

}
