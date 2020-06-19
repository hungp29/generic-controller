package org.example.genericcontroller.config;

import org.example.genericcontroller.support.generic.jpa.GenericRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA Configuration.
 *
 * @author hungp
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "org.example.genericcontroller.app",
        repositoryFactoryBeanClass = GenericRepositoryFactoryBean.class
)
public class JpaConfiguration {
}
