package org.example.genericcontroller.support.generic.proxy.process.response;

import org.example.genericcontroller.support.generic.proxy.ProcessResponse;
import org.example.genericcontroller.support.generic.utils.ControllerUtils;
import org.example.genericcontroller.support.generic.utils.MappingUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Process Response for create method.
 *
 * @author hungp
 */
@Component
public class ProcessResponseCreateMethod extends ProcessResponse {

    @Override
    public Object convertResponse(Object result, Class<?> controllerType) {
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
