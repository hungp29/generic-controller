package org.example.genericcontroller.support.generic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.genericcontroller.support.generic.annotation.APIGeneric;
import org.example.genericcontroller.support.generic.mapping.DTOMapping;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GenericConfiguration implements ImportAware, InitializingBean {

    @Nullable
    protected AnnotationAttributes enableGeneric;

    private final RequestMappingHandlerMapping handlerMapping;

    private final GenericWebApplicationContext context;

    protected String packageScan;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableGeneric = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnabledGeneric.class.getName(), false));
        if (this.enableGeneric == null) {
            throw new IllegalArgumentException(
                    "@EnabledGeneric is not present on importing class " + importMetadata.getClassName());
        }
        packageScan = enableGeneric.getString(EnabledGeneric.SCAN_ATTRIBUTE);
        if (StringUtils.isEmpty(packageScan)) {
            AnnotatedGenericBeanDefinition configBeanDef = new AnnotatedGenericBeanDefinition(importMetadata);
            packageScan = resolveBasePackage(Objects.requireNonNull(configBeanDef.getBeanClassName()));
        }
    }

    private String resolveBasePackage(String beanClassName) {
        return beanClassName.lastIndexOf(".") > 0 ? beanClassName.substring(0, beanClassName.lastIndexOf(".")) : "";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == enableGeneric) {
            List<RequestMappingInfo> unregister = new LinkedList<>();
            handlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
                if (AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), APIGeneric.class)) {
                    log.info("[Generic Endpoint] Disable API: " + requestMappingInfo.toString());
                    unregister.add(requestMappingInfo);
                }
            });
            unregister.forEach(handlerMapping::unregisterMapping);
        }
    }

    @Bean
    public DTOMappingCache scanDTOBean() {
        if (null != enableGeneric) {
            return doScan(packageScan);
        }
        return null;
    }

    private DTOMappingCache doScan(String packageScan) {
        Assert.notNull(packageScan, "Package scan must be not null");
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(MappingClass.class));

        DTOMappingCache mappingCache = new DTOMappingCache();
        for (BeanDefinition bd : scanner.findCandidateComponents(packageScan)) {
            log.info("[Generic DTO] Building mapping DTO object {}", bd.getBeanClassName());
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                DTOMapping dtoMapping = DTOMapping.of(Class.forName(bd.getBeanClassName()), mappingCache);
//                context.registerBean(bd.getBeanClassName(), clazz, dtoMapping);
                mappingCache.put(dtoMapping);
            } catch (ClassNotFoundException e) {
                log.warn("Cannot found class {}", bd.getBeanClassName());
            }
        }

        return mappingCache;
    }
}
