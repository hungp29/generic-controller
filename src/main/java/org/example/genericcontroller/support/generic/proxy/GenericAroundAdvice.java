package org.example.genericcontroller.support.generic.proxy;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.catalina.connector.RequestFacade;
import org.aspectj.lang.ProceedingJoinPoint;
import org.example.genericcontroller.support.generic.annotation.APIGeneric;
import org.example.genericcontroller.support.generic.annotation.APIGeneric.APIGenericMethod;
import org.example.genericcontroller.utils.ObjectUtils;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        Object[] args = buildArguments(invocation);
        if (isMethod(invocation, APIGenericMethod.CREATE)) {
            args = processArgument.prepareArgumentsForCreateMethod(args, entityType, controllerType);
            result = processResponse.convertResponseForCreateMethod(joinPoint.proceed(args), controllerType);
        } else if (isMethod(invocation, APIGenericMethod.READ_ALL)) {
            args = processArgument.prepareArgumentsForReadAllMethod(args, entityType, controllerType);
            result = joinPoint.proceed(args);
        } else if (isMethod(invocation, APIGenericMethod.READ_ONE)) {
            args = processArgument.prepareArgumentsForReadOneMethod(args, entityType, controllerType);
            result = joinPoint.proceed(args);
        } else {
            result = joinPoint.proceed();
        }

        return result;
    }

    /**
     * Build Arguments.
     *
     * @param invocation {@link MethodInvocation} instance
     * @return array arguments
     */
    private Object[] buildArguments(MethodInvocation invocation) {
        List<Object> args = Arrays.stream(invocation.getArguments())
                .filter(arg -> null == arg || !RequestFacade.class.isAssignableFrom(arg.getClass()))
                .collect(Collectors.toList());
        args.add(getHttpServletRequest());
        return args.toArray();
    }

    /**
     * Get {@link HttpServletRequest} from {@link RequestContextHolder}.
     *
     * @return {@link HttpServletRequest}
     */
    protected HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
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
     * Check API generic method is match with {@link APIGenericMethod} param.
     *
     * @param invocation    {@link MethodInvocation} instance
     * @param genericMethod {@link APIGenericMethod} method need check
     * @return true if method is match {@link APIGenericMethod} specify
     */
    private boolean isMethod(MethodInvocation invocation, APIGenericMethod genericMethod) {
        APIGeneric apiGeneric = ObjectUtils.getAnnotation(invocation.getMethod(), APIGeneric.class, true);
        return null != apiGeneric && apiGeneric.genericMethod().equals(genericMethod);
    }
}
