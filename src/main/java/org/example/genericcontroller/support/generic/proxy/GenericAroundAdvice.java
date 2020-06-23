package org.example.genericcontroller.support.generic.proxy;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.example.genericcontroller.exception.generic.ParamInvalidException;
import org.example.genericcontroller.support.generic.APICreate;
import org.example.genericcontroller.support.generic.utils.EntityUtils;
import org.example.genericcontroller.utils.ObjectUtils;
import org.springframework.aop.framework.ReflectiveMethodInvocation;

import java.util.Map;

/**
 * Generic around advice.
 *
 * @author hungp
 */
@Slf4j
public class GenericAroundAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("Proxy for " + invocation.getThis().getClass().getName() + "." + invocation.getMethod().getName());
        Class<?> entityType = ObjectUtils.getGenericClass(invocation.getThis().getClass());
        if (isCreateMethod(invocation)) {
            Class<?> dtoType = EntityUtils.getCreateRequestDTO(entityType);
            ReflectiveMethodInvocation reflectiveMethodInvocation = (ReflectiveMethodInvocation) invocation;
            reflectiveMethodInvocation.setArguments(convertToDataTransferObject(invocation.getArguments()[0], dtoType));
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

    /**
     * Convert Map data to Data Transfer Object.
     *
     * @param data    map data
     * @param dtoType Data Transfer Object type
     * @return Data Transfer Object instance
     */
    @SuppressWarnings("unchecked")
    private Object convertToDataTransferObject(Object data, Class<?> dtoType) {
        if (Map.class.isAssignableFrom(data.getClass())) {
            return ObjectUtils.convertMapToObject((Map<String, ?>) data, dtoType);
        }
        throw new ParamInvalidException("Cannot parse '" + data.getClass().getName() + "' to '" + dtoType.getName() + "'");
    }
}
