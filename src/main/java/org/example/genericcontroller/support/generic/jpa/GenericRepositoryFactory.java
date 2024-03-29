package org.example.genericcontroller.support.generic.jpa;

import org.example.genericcontroller.support.generic.GenericSpecification;
import org.example.genericcontroller.support.generic.SimpleGenericRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Generic Repository Factory.
 *
 * @author hungp
 */
public class GenericRepositoryFactory extends JpaRepositoryFactory {

    private final EntityManager entityManager;

    private final GenericSpecification spec;

    /**
     * Creates a new {@link JpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public GenericRepositoryFactory(EntityManager entityManager, GenericSpecification spec) {
        super(entityManager);
        this.entityManager = entityManager;
        this.spec = spec;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
        return new SimpleGenericRepository(entityInformation, entityManager, spec);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleGenericRepository.class;
    }
}
