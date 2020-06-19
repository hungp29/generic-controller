package org.example.genericcontroller.support.generic.jpa;

import org.example.genericcontroller.support.generic.DefaultGenericSpecification;
import org.example.genericcontroller.support.generic.GenericSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Generic Repository Factory Bean.
 *
 * @param <T>  Generic of Repository
 * @param <S>  Generic of Entity
 * @param <ID> Generic of Id of Entity
 * @author hungp
 */
public class GenericRepositoryFactoryBean<T extends JpaRepository<S, ID>, S, ID extends Serializable>
        extends JpaRepositoryFactoryBean<T, S, ID> {

    /**
     * Generic Specification.
     */
    private GenericSpecification spec;

    /**
     * Creates a new {@link JpaRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public GenericRepositoryFactoryBean(Class<? extends T> repositoryInterface, @Nullable GenericSpecification spec) {
        super(repositoryInterface);
        if (null != spec) {
            this.spec = spec;
        } else {
            this.spec = new DefaultGenericSpecification();
        }
    }

    /**
     * Returns a {@link RepositoryFactorySupport}.
     *
     * @param entityManager Entity Manager
     * @return {@link RepositoryFactorySupport}
     */
    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new GenericRepositoryFactory(entityManager, spec);
    }
}
