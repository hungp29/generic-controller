package org.example.genericcontroller.support.generic.proxy;

import org.example.genericcontroller.support.generic.GenericRestController;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Proxy Configuration.
 *
 * @author hungp
 */
@Configuration
public class ProxyConfiguration {

    private final ProcessArgument processArgument;

    public ProxyConfiguration(ProcessArgument processArgument) {
        this.processArgument = processArgument;
    }

    /**
     * Create Auto Proxy Creator bean.
     *
     * @return AutoProxyCreator instance
     */
    @Bean
    public AbstractAutoProxyCreator autoProxyCreator() {
        return new AbstractAutoProxyCreator() {
            @Override
            protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName,
                                                            TargetSource customTargetSource) throws BeansException {
                if (GenericRestController.class.isAssignableFrom(beanClass)) {
                    return new Object[]{genericAroundAdvice(processArgument)};
                } else {
                    return DO_NOT_PROXY;
                }
            }
        };
    }


    /**
     * Create Generic Around Advice.
     *
     * @param processArgument {@link ProcessArgument} instance
     * @return {@link GenericAroundAdvice} instance
     */
    @Bean
    public GenericAroundAdvice genericAroundAdvice(ProcessArgument processArgument) {
        return new GenericAroundAdvice(processArgument);
    }
}
