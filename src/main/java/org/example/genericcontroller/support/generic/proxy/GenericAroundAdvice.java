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

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.debug("Proxy for " + invocation.getThis().getClass().getName() + "." + invocation.getMethod().getName());

        ProxyMethodInvocation proxyMethodInvocation = (ProxyMethodInvocation) invocation;
        ProceedingJoinPoint joinPoint = lazyGetProceedingJoinPoint(proxyMethodInvocation);
        Class<?> controllerType = invocation.getThis().getClass();
        Class<?> entityType = ObjectUtils.getGenericClass(controllerType);

        Object result;
        // Prepare data for create method
        Object[] args = buildArguments(invocation);

        ProcessArgument processArgument = ProcessArgumentFactory.getProcessArgument(invocation);
        if (null != processArgument) {
            args = processArgument.prepareArguments(args, entityType, controllerType);
            result = joinPoint.proceed(args);
        } else {
            result = joinPoint.proceed();
        }

        ProcessResponse processResponse = ProcessResponseFactory.getProcessResponse(invocation);
        if (null != processResponse) {
            result = processResponse.convertResponse(result, controllerType);
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
}
