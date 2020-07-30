package org.example.genericcontroller.support.generic.proxy.process.argument;

import org.example.genericcontroller.support.generic.Pagination;
import org.example.genericcontroller.support.generic.RootFilterData;
import org.example.genericcontroller.support.generic.exception.ArgumentException;
import org.example.genericcontroller.support.generic.proxy.ProcessArgument;
import org.example.genericcontroller.support.generic.utils.ControllerUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class ProcessArgumentReadAllMethod extends ProcessArgument {

    @Override
    public Object[] prepareArguments(Object[] args, Class<?> entityType, Class<?> controllerType) {
        Class<?> readDTOType = null;
        Map<String, String> params = null;
        Pagination pagination = null;
        String[] filter = null;

        if (null != args && args.length > 0 && null != entityType) {
            // 1. ReadDTOType
            readDTOType = ControllerUtils.getReadResponseDTO(controllerType);
            HttpServletRequest request = getHttpServletRequest(args);
            if (null != request) {
                // 2. Map params
                params = getParameters(request);
                // 3. Pagination info
                Sort sort = getSortRequest(request);
                Pageable pageable = getPageRequest(request, sort);
                pagination = new Pagination(pageable, sort);
                // 4. Filter field array
                filter = getFilterFields(request);
            } else {
                throw new ArgumentException("Cannot find HttpServletRequest in array arguments of method");
            }
        }
        RootFilterData rootFilterData = new RootFilterData(readDTOType, filter, params);

        return new Object[]{readDTOType, params, pagination, filter, rootFilterData};
    }
}
