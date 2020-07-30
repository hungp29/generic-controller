package org.example.genericcontroller.support.generic.proxy.process.argument;

import lombok.extern.slf4j.Slf4j;
import org.example.genericcontroller.support.generic.exception.ArgumentException;
import org.example.genericcontroller.support.generic.proxy.ProcessArgument;
import org.example.genericcontroller.support.generic.utils.ControllerUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Process Argument for read one method.
 *
 * @author hungp
 */
@Slf4j
@Component
public class ProcessArgumentReadOneMethod extends ProcessArgument {

    @Override
    public Object[] prepareArguments(Object[] args, Class<?> entityType, Class<?> controllerType) {
        log.debug("Start prepare arguments for Read One Method of " + controllerType.getSimpleName());
        Class<?> readDTOType = null;
        Object id = null;
        String[] filter = null;

        if (null != args && args.length > 0 && null != entityType) {
            // 1. ID
            id = args[0];
            // 2. ReadDTOType
            readDTOType = ControllerUtils.getReadResponseDTO(controllerType);
            HttpServletRequest request = getHttpServletRequest(args);
            if (null != request) {
                // 3. Filter field array
                filter = getFilterFields(request);
            } else {
                throw new ArgumentException("Cannot find HttpServletRequest in array arguments of method");
            }
        }

        return new Object[]{id, readDTOType, filter};
    }
}
