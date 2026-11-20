package pet.project.database.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pet.project.database.entity.User;

import java.util.Optional;


@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final EntityManager entityManager;

    @Transactional
    public User save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByUsername(String username){
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.username = :username",
                        User.class)
                .setParameter("username",username)
                .getResultStream()
                .findFirst();
    }
}