package org.example.genericcontroller.support.version;

import lombok.extern.slf4j.Slf4j;
import org.example.genericcontroller.support.generic.api.APIGeneric;
import org.example.genericcontroller.support.generic.GenericDisabled;
import org.example.genericcontroller.utils.ObjectUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

import static org.example.genericcontroller.support.generic.api.APIGeneric.APIGenericMethod.*;

/**
 * Version Request Mapping Handler.
 *
 * @author hungp
 */
@Slf4j
public class VersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        if (!disabledGenericMethod(method, handlerType)) {
            return super.getMappingForMethod(method, handlerType);
        }
        return null;
    }

    /**
     * Check disabled generic method.
     *
     * @param method      method instance
     * @param handlerType handler type
     * @return true if generic method is disabled
     */
    private boolean disabledGenericMethod(Method method, Class<?> handlerType) {
        GenericDisabled genericDisabled = ObjectUtils.getAnnotation(handlerType, GenericDisabled.class);
        APIGeneric apiGeneric = ObjectUtils.getAnnotation(method, APIGeneric.class, true);
        if (null != genericDisabled && null != apiGeneric) {
            return (CREATE.equals(apiGeneric.genericMethod()) && genericDisabled.create()) ||
                    (READ_ONE.equals(apiGeneric.genericMethod()) && genericDisabled.read()) ||
                    (READ_ALL.equals(apiGeneric.genericMethod()) && genericDisabled.readAll()) ||
                    (UPDATE.equals(apiGeneric.genericMethod()) && genericDisabled.update()) ||
                    (DELETE.equals(apiGeneric.genericMethod()) && genericDisabled.delete());
        }
        return false;
    }
}
