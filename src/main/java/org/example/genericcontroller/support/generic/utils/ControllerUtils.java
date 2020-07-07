package org.example.genericcontroller.support.generic.utils;

import org.example.genericcontroller.support.generic.exception.ConfigurationInvalidException;
import org.example.genericcontroller.support.generic.DataTransferObjectMapping;
import org.example.genericcontroller.utils.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller Utils.
 *
 * @author hungp
 */
public class ControllerUtils {

    /**
     * Prevent new instance.
     */
    private ControllerUtils() {
    }

    /**
     * Validate controller type has RestController and DataTransferObjectMapping annotations.
     *
     * @param controllerType Controller type
     * @return true if Controller class has RestController and DataTransferObjectMapping annotations, otherwise return false
     */
    public static boolean validate(Class<?> controllerType) {
        return ObjectUtils.hasAnnotation(controllerType, RestController.class) &&
                ObjectUtils.hasAnnotation(controllerType, DataTransferObjectMapping.class);
    }

    /**
     * Validate Controller type. If Controller class don't has RestController and DataTransferObjectMapping annotations
     * then throw Runtime Exception.
     *
     * @param controllerType Controller type
     * @param thr            Exception to throw if Controller type is invalid
     */
    public static void validateThrow(Class<?> controllerType, RuntimeException thr) {
        if (!validate(controllerType)) {
            throw thr;
        }
    }

    /**
     * Get DataTransferObjectMapping annotation from Controller.
     *
     * @param controllerType controller type
     * @return {@link DataTransferObjectMapping} instance
     */
    public static DataTransferObjectMapping getDataTransferObjectMapping(Class<?> controllerType) {
        validateThrow(controllerType, new ConfigurationInvalidException(controllerType.getName() + ": Controller configuration is invalid"));
        return ObjectUtils.getAnnotation(controllerType, DataTransferObjectMapping.class);
    }

    /**
     * Get Create Request DTO.
     *
     * @param controllerType Controller type
     * @return Create Request DTO
     */
    public static Class<?> getCreateRequestDTO(Class<?> controllerType) {
        return getDataTransferObjectMapping(controllerType).forCreateRequest();
    }

    /**
     * Get Create Response DTO.
     *
     * @param controllerType Controller type
     * @return Create Request DTO
     */
    public static Class<?> getCreateResponseDTO(Class<?> controllerType) {
        return getDataTransferObjectMapping(controllerType).forCreateResponse();
    }

    /**
     * Get Read Response DTO.
     *
     * @param controllerType Controller type
     * @return Create Request DTO
     */
    public static Class<?> getReadResponseDTO(Class<?> controllerType) {
        return getDataTransferObjectMapping(controllerType).forRead();
    }
}
