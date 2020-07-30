package org.example.genericcontroller.support.generic.proxy;

/**
 * Process Response.
 *
 * @author hungp
 */
public abstract class ProcessResponse {

    /**
     * Convert result of Generic API to response.
     *
     * @param result         result of method
     * @param controllerType controller type
     * @return response
     */
    public abstract Object convertResponse(Object result, Class<?> controllerType);
}
