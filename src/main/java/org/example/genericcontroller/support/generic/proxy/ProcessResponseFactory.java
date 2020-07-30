package org.example.genericcontroller.support.generic.proxy;

import org.aopalliance.intercept.MethodInvocation;
import org.example.genericcontroller.support.generic.annotation.APIGeneric;
import org.example.genericcontroller.support.generic.proxy.process.response.ProcessResponseCreateMethod;
import org.example.genericcontroller.utils.SpringContext;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * Process Response Factory.
 *
 * @author hungp
 */
public class ProcessResponseFactory {

    /**
     * Prevents new instance {@link ProcessResponseFactory}.
     */
    private ProcessResponseFactory() {
    }

    /**
     * Get {@link ProcessResponse} instance base on {@link MethodInvocation}.
     *
     * @param invocation {@link MethodInvocation}
     * @return {@link ProcessResponse}
     */
    public static ProcessResponse getProcessResponse(MethodInvocation invocation) {
        APIGeneric generic = AnnotatedElementUtils.findMergedAnnotation(invocation.getMethod(), APIGeneric.class);
        if (null != generic) {
            switch (generic.genericMethod()) {
                case CREATE:
                    return SpringContext.getBean(ProcessResponseCreateMethod.class);
                default:
                    return null;
            }
        }
        return null;
    }
}
