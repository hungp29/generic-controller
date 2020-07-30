package org.example.genericcontroller.support.generic.proxy;

import org.aopalliance.intercept.MethodInvocation;
import org.example.genericcontroller.support.generic.annotation.APIGeneric;
import org.example.genericcontroller.support.generic.proxy.process.argument.ProcessArgumentCreateMethod;
import org.example.genericcontroller.support.generic.proxy.process.argument.ProcessArgumentReadAllMethod;
import org.example.genericcontroller.support.generic.proxy.process.argument.ProcessArgumentReadOneMethod;
import org.example.genericcontroller.utils.SpringContext;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * Process Argument Factory.
 *
 * @author hungp
 */
public class ProcessArgumentFactory {

    /**
     * Prevents new {@link ProcessArgumentFactory} instance.
     */
    private ProcessArgumentFactory() {
    }

    /**
     * Get {@link ProcessArgument} instance base on {@link MethodInvocation}.
     *
     * @param invocation {@link MethodInvocation}
     * @return {@link ProcessArgument}
     */
    public static ProcessArgument getProcessArgument(MethodInvocation invocation) {
        APIGeneric generic = AnnotatedElementUtils.findMergedAnnotation(invocation.getMethod(), APIGeneric.class);
        if (null != generic) {
            switch (generic.genericMethod()) {
                case CREATE:
                    return SpringContext.getBean(ProcessArgumentCreateMethod.class);
                case READ_ONE:
                    return SpringContext.getBean(ProcessArgumentReadOneMethod.class);
                case READ_ALL:
                    return SpringContext.getBean(ProcessArgumentReadAllMethod.class);
                default:
                    return null;
            }
        }
        return null;
    }
}
