package org.example.genericcontroller.support.generic.proxy;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.example.genericcontroller.support.generic.APICreate;
import org.example.genericcontroller.support.generic.utils.EntityUtils;
import org.example.genericcontroller.utils.ObjectUtils;

/**
 * Generic around advice.
 *
 * @author hungp
 */
@Slf4j
public class GenericAroundAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("Proxy for " + invocation.getMethod().getName());
        Class<?> entityType = ObjectUtils.getGenericClass(invocation.getThis().getClass());
        if (isCreateMethod(invocation)) {
            Class<?> dtoType = EntityUtils.getCreateRequestDTO(entityType);
            System.out.println(dtoType.getName());
        }
        return invocation.proceed();
    }

    /**
     * Checking method is create API or not.
     *
     * @param invocation Method Invocation
     * @return true if method is create API
     */
    private boolean isCreateMethod(MethodInvocation invocation) {
        return ObjectUtils.hasAnnotation(invocation.getMethod(), APICreate.class);
    }
}
