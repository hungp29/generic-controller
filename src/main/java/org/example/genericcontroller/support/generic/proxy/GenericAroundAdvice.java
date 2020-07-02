package org.example.genericcontroller.support.generic.proxy;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.example.genericcontroller.exception.generic.ParamInvalidException;
import org.example.genericcontroller.support.generic.APICreate;
import org.example.genericcontroller.support.generic.APIReadAll;
import org.example.genericcontroller.support.generic.APIReadOne;
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
    private final ProcessResponse processResponse;

    public GenericAroundAdvice(ProcessArgument processArgument, ProcessResponse processResponse) {
        this.processArgument = processArgument;
        this.processResponse = processResponse;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("Proxy for " + invocation.getThis().getClass().getName() + "." + invocation.getMethod().getName());

        ProxyMethodInvocation proxyMethodInvocation = (ProxyMethodInvocation) invocation;
        ProceedingJoinPoint joinPoint = lazyGetProceedingJoinPoint(proxyMethodInvocation);
        Class<?> controllerType = invocation.getThis().getClass();
        Class<?> entityType = ObjectUtils.getGenericClass(controllerType);

        Object result;
        // Prepare data for create method
        Object[] args;
        if (isCreateMethod(invocation)) {
            args = processArgument.prepareArgumentsForCreateMethod(invocation.getArguments(), entityType, controllerType);
            result = processResponse.convertResponseForCreateMethod(joinPoint.proceed(args), controllerType);
        } else if (isReadAllMethod(invocation)) {
            args = processArgument.prepareArgumentsForReadAllMethod(invocation.getArguments(), entityType, controllerType);
            result = joinPoint.proceed(args);
        } else if (isReadOneMethod(invocation)) {
            args = processArgument.prepareArgumentsForReadOneMethod(invocation.getArguments(), entityType, controllerType);
            result = joinPoint.proceed(args);
        } else {
            result = joinPoint.proceed();
        }

        return result;
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
     * Checking method is read one API or not.
     *
     * @param invocation {@link MethodInvocation} instance
     * @return true if method is read all method
     */
    private boolean isReadOneMethod(MethodInvocation invocation) {
        return ObjectUtils.hasAnnotation(invocation.getMethod(), APIReadOne.class);
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
