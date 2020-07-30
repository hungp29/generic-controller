package org.example.genericcontroller.support.generic.proxy.process.argument;

import org.example.genericcontroller.support.generic.proxy.ProcessArgument;
import org.example.genericcontroller.support.generic.utils.ControllerUtils;
import org.springframework.stereotype.Component;

@Component
public class ProcessArgumentCreateMethod extends ProcessArgument {

    @Override
    public Object[] prepareArguments(Object[] args, Class<?> entityType, Class<?> controllerType) {
        Object createRequestDTO = null;
        if (null != args && args.length > 0 && null != entityType) {
            Class<?> dtoType = ControllerUtils.getCreateRequestDTO(controllerType);
            createRequestDTO = convertToDataTransferObject(args[0], dtoType);
        }
        return new Object[]{createRequestDTO};
    }
}
