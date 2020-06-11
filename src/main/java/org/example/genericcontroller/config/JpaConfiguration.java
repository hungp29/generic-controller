package org.example.genericcontroller.config;

import org.example.genericcontroller.support.generic.DefaultRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "org.example.genericcontroller.app",
        repositoryBaseClass = DefaultRepositoryImpl.class)
public class JpaConfiguration {
}
