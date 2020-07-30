package org.example.genericcontroller.support.generic.proxy.process.argument;

import lombok.extern.slf4j.Slf4j;
import org.example.genericcontroller.support.generic.proxy.ProcessArgument;
import org.example.genericcontroller.support.generic.utils.ControllerUtils;
import org.springframework.stereotype.Component;

/**
 * Process Argument for create method.
 *
 * @author hungp
 */
@Slf4j
@Component
public class ProcessArgumentCreateMethod extends ProcessArgument {

    @Override
    public Object[] prepareArguments(Object[] args, Class<?> entityType, Class<?> controllerType) {
        log.debug("Start prepare arguments for Create Method of " + controllerType.getSimpleName());
        Object createRequestDTO = null;
        if (null != args && args.length > 0 && null != entityType) {
            Class<?> dtoType = ControllerUtils.getCreateRequestDTO(controllerType);
            createRequestDTO = convertToDataTransferObject(args[0], dtoType);
        }
        return new Object[]{createRequestDTO};
    }
}
