package org.example.genericcontroller.support.generic.proxy;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.example.genericcontroller.exception.generic.ParamInvalidException;
import org.example.genericcontroller.support.generic.APICreate;
import org.example.genericcontroller.support.generic.APIReadAll;
import org.example.genericcontroller.utils.ObjectUtils;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;

import java.util.Map;

/**
 * Generic around advice.
 *
 * @author hungp
 */
@Slf4j
public class GenericAroundAdvice implements MethodInterceptor {

    private final ProcessArgument processArgument;

    public GenericAroundAdvice(ProcessArgument processArgument) {
        this.processArgument = processArgument;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("Proxy for " + invocation.getThis().getClass().getName() + "." + invocation.getMethod().getName());

        ProxyMethodInvocation proxyMethodInvocation = (ProxyMethodInvocation) invocation;
        ProceedingJoinPoint joinPoint = lazyGetProceedingJoinPoint(proxyMethodInvocation);
        Class<?> entityType = ObjectUtils.getGenericClass(invocation.getThis().getClass());

        // Prepare data for create method
        Object[] args = null;
        if (isCreateMethod(invocation)) {
            args = processArgument.prepareArgumentsForCreateMethod(invocation.getArguments(), entityType);
        } else if (isReadAllMethod(invocation)) {
            args = processArgument.prepareArgumentsForReadAllMethod(invocation.getArguments(), entityType);
        }

        return joinPoint.proceed(args);
    }

    /**
     * Lazy get proceeding join point.
     *
     * @param rmi {@link ProxyMethodInvocation} instance
     * @return {@link ProceedingJoinPoint} instance
     */
    protected ProceedingJoinPoint lazyGetProceedingJoinPoint(ProxyMethodInvocation rmi) {
        return new MethodInvocationProceedingJoinPoint(rmi);
    }

    /**
     * Checking method is create API or not.
     *
     * @param invocation {@link MethodInvocation} instance
     * @return true if method is create API
     */
    private boolean isCreateMethod(MethodInvocation invocation) {
        return ObjectUtils.hasAnnotation(invocation.getMethod(), APICreate.class);
    }

    /**
     * Checking method is read all API or not.
     *
     * @param invocation {@link MethodInvocation} instance
     * @return true if method is read all method
     */
    private boolean isReadAllMethod(MethodInvocation invocation) {
        return ObjectUtils.hasAnnotation(invocation.getMethod(), APIReadAll.class);
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
