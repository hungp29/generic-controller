package org.example.genericcontroller.config;

import lombok.RequiredArgsConstructor;
import org.example.genericcontroller.support.version.VersionRequestMappingHandlerMapping;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * App Configuration.
 *
 * @author hungp
 */
@Configuration
@RequiredArgsConstructor
public class AppConfiguration extends WebMvcConfigurationSupport {

    /**
     * Custom Request Mapping Handler Mapping with version.
     *
     * @return RequestMappingHandlerMapping
     */
    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping(ContentNegotiationManager contentNegotiationManager, FormattingConversionService conversionService, ResourceUrlProvider resourceUrlProvider) {
        VersionRequestMappingHandlerMapping versionMapping = new VersionRequestMappingHandlerMapping();
        versionMapping.setOrder(0);
        versionMapping.setInterceptors(getInterceptors(conversionService, resourceUrlProvider));
        versionMapping.setContentNegotiationManager(mvcContentNegotiationManager());
        versionMapping.setUseTrailingSlashMatch(false);
        versionMapping.setUseSuffixPatternMatch(false);
        return versionMapping;
    }
}
