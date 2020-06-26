package org.example.genericcontroller.support.generic.proxy;

import org.example.genericcontroller.support.generic.utils.ControllerUtils;
import org.example.genericcontroller.support.generic.utils.MappingUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Process Response.
 *
 * @author hungp
 */
@Component
public class ProcessResponse {

    /**
     * Convert Response For Read All Method.
     *
     * @param result         response
     * @param controllerType controller type
     * @return result after converted
     */
    public Object convertResponseForCreateMethod(Object result, Class<?> controllerType) {
        if (null != result && null != controllerType) {
            boolean isResponseEntity = ResponseEntity.class.isAssignableFrom(result.getClass());
            Class<?> dtoType = ControllerUtils.getCreateResponseDTO(controllerType);
            if (isResponseEntity) {
                ResponseEntity<?> responseEntity = ((ResponseEntity<?>) result);
                Object entityConverted = MappingUtils.convertEntityToDataTransferObject(responseEntity.getBody(), dtoType);
                result = null != entityConverted ? ResponseEntity.ok(entityConverted) : ResponseEntity.noContent();
            } else {
                result = MappingUtils.convertEntityToDataTransferObject(result, dtoType);
            }
        }
        return result;
    }
}
