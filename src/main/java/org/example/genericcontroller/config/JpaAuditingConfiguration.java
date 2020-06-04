package org.example.genericcontroller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Configuration audit for JPA.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfiguration {

    @Bean
    public AuditorAware<Integer> auditorProvider() {
        return new AuditorAwareImpl();
    }

    /**
     * Implementation AuditorAware.
     */
    public static class AuditorAwareImpl implements AuditorAware<Integer> {

        @Override
        public Optional<Integer> getCurrentAuditor() {
            return Optional.empty();
        }
    }
}
