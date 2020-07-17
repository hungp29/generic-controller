package org.example.genericcontroller.support.generic.jpa;

import org.example.genericcontroller.support.generic.DTOMappingCache;
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

    private final DTOMappingCache mappingCache;

    /**
     * Creates a new {@link JpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     * @param spec          {@link GenericSpecification}
     * @param mappingCache  {@link DTOMappingCache}
     */
    public GenericRepositoryFactory(EntityManager entityManager, GenericSpecification spec, DTOMappingCache mappingCache) {
        super(entityManager);
        this.entityManager = entityManager;
        this.spec = spec;
        this.mappingCache = mappingCache;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
        return new SimpleGenericRepository(entityInformation, entityManager, spec, mappingCache);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleGenericRepository.class;
    }
}
