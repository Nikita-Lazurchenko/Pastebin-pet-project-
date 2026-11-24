package pet.project.database.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pet.project.database.entity.Paste;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class PasteRepository{
    private final EntityManager entityManager;

    @Transactional
    public Paste save(Paste paste) {
        if (paste.getId() == null) {
            entityManager.persist(paste);
            return paste;
        } else {
            return entityManager.merge(paste);
        }
    }

    @Transactional
    public Optional<Paste> findByHash(String hash){
        return Optional.ofNullable(entityManager.createQuery(
                "SELECT p FROM Paste p WHERE p.pasteHash = :hash", Paste.class)
                .setParameter("hash",hash)
                .getSingleResult());
    }
}