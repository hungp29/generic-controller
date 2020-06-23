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
                    return new Object[]{genericAroundAdvice()};
                } else {
                    return DO_NOT_PROXY;
                }
            }
        };
    }


    /**
     * Create Generic Around Advice.
     *
     * @return Generic Around Advice instance
     */
    @Bean
    public GenericAroundAdvice genericAroundAdvice() {
        return new GenericAroundAdvice();
    }
}