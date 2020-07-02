package org.example.genericcontroller.support.generic.proxy;

import org.apache.catalina.connector.RequestFacade;
import org.example.genericcontroller.exception.generic.ArgumentException;
import org.example.genericcontroller.exception.generic.GenericException;
import org.example.genericcontroller.exception.generic.ParamInvalidException;
import org.example.genericcontroller.support.generic.Pagination;
import org.example.genericcontroller.support.generic.utils.ControllerUtils;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Process Argument.
 *
 * @author hungp
 */
@Component
public class ProcessArgument {

    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final String FILTER = "filter";
    public static final String ORDER_BY = "orderBy";
    public static final String[] NOT_PARAM_FIELDS = {PAGE, LIMIT, ORDER_BY, FILTER};

    private int defaultPage = 1;
    private int defaultLimit = 10;

    /**
     * Prepare Arguments for create method.
     *
     * @param args       array argument
     * @param entityType Entity type
     * @return array argument
     */
    public Object[] prepareArgumentsForCreateMethod(Object[] args, Class<?> entityType, Class<?> controllerType) {
        Object createRequestDTO = null;
        if (null != args && args.length > 0 && null != entityType) {
            Class<?> dtoType = ControllerUtils.getCreateRequestDTO(controllerType);
            createRequestDTO = convertToDataTransferObject(args[0], dtoType);
        }
        return new Object[]{createRequestDTO};
    }

    /**
     * Prepare arguments for read all method.
     * 1. ReadDTOType
     * 2. Map params
     * 3. Pagination info
     * 4. Filter field array
     *
     * @param args       array arguments
     * @param entityType Entity type
     * @return array arguments
     */
    public Object[] prepareArgumentsForReadAllMethod(Object[] args, Class<?> entityType, Class<?> controllerType) {
        Class<?> readDTOType = null;
        Map<String, String> params = null;
        Pagination pagination = null;
        String[] filter = null;
        HttpServletRequest request = null;

        if (null != args && args.length > 0 && null != entityType) {
            // 1. ReadDTOType
            readDTOType = ControllerUtils.getReadResponseDTO(controllerType);
            request = getHttpServletRequest(args);
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

        return new Object[]{readDTOType, params, pagination, filter, request};
    }

    /**
     * Prepare arguments for read one method.
     * 1. ID
     * 2. ReadDTOType
     * 3. Filter field array
     *
     * @param args       array arguments
     * @param entityType Entity type
     * @return array arguments
     */
    public Object[] prepareArgumentsForReadOneMethod(Object[] args, Class<?> entityType, Class<?> controllerType) {
        Class<?> readDTOType = null;
        Object id = null;
        String[] filter = null;
        HttpServletRequest request = null;

        if (null != args && args.length > 0 && null != entityType) {
            // 1. ID
            id = args[0];
            // 2. ReadDTOType
            readDTOType = ControllerUtils.getReadResponseDTO(controllerType);
            request = getHttpServletRequest(args);
            if (null != request) {
                // 3. Filter field array
                filter = getFilterFields(request);
            } else {
                throw new ArgumentException("Cannot find HttpServletRequest in array arguments of method");
            }
        }

        return new Object[]{id, readDTOType, filter, request};
    }

    /**
     * Get map parameters from Http Servlet Request.
     *
     * @param request Http Servlet Request
     * @return map params
     */
    private Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String paramName = entry.getKey();
            if (!Arrays.asList(NOT_PARAM_FIELDS).contains(paramName)) {
                String paramValue = null != entry.getValue() ? String.join(Constants.COMMA, entry.getValue()) : Constants.EMPTY_STRING;
                params.put(paramName, paramValue);
            }
        }
        return params;
    }

    /**
     * Get {@link Pageable} instance from {@link HttpServletRequest}.
     *
     * @param request {@link HttpServletRequest} http request
     * @return {@link Pageable} instance
     */
    private Pageable getPageRequest(HttpServletRequest request, Sort sort) {
        String pageValue = getValueFromRequest(PAGE, request);
        String limitValue = getValueFromRequest(LIMIT, request);
        Integer page = null;
        Integer limit = null;
        String sortValue = getValueFromRequest(ORDER_BY, request);
        try {
            if (!StringUtils.isEmpty(pageValue)) {
                page = Integer.parseInt(pageValue);
            }
            if (!StringUtils.isEmpty(limitValue)) {
                limit = Integer.parseInt(limitValue);
            }
        } catch (NumberFormatException e) {
            throw new GenericException("Cannot convert value for paging");
        }
        return buildPageRequest(page, limit, sort);
    }

    /**
     * Get sort from {@link HttpServletRequest}.
     *
     * @param request {@link HttpServletRequest} http request
     * @return {@link Sort} instance
     */
    private Sort getSortRequest(HttpServletRequest request) {
        String sortValue = getValueFromRequest(ORDER_BY, request);
        if (!StringUtils.isEmpty(sortValue)) {
            return buildSort(sortValue);
        }
        return Sort.unsorted();
    }

    /**
     * Get filter field.
     *
     * @param request Http Servlet Request
     * @return return filter fields array
     */
    private String[] getFilterFields(HttpServletRequest request) {
        String filterFieldsValue = request.getParameter(FILTER);
        if (!StringUtils.isEmpty(filterFieldsValue)) {
            return StringUtils.trimArrayElements(filterFieldsValue.split(Constants.COMMA));
        }
        return null;
    }

    /**
     * Build page request.
     *
     * @param page  page
     * @param limit limit
     * @param sort  {@link Sort} instance
     * @return {@link Pageable} instance
     */
    private Pageable buildPageRequest(Integer page, Integer limit, Sort sort) {
        Pageable pageable = Pageable.unpaged();
        if (null != page) {
            limit = null != limit ? limit : defaultLimit;
            pageable = PageRequest.of(page - defaultPage, limit, sort);
        }
        return pageable;
    }

    /**
     * Build sort with pattern .
     *
     * @param sort sort value
     * @return {@link Sort} instance
     */
    private Sort buildSort(String sort) {
        Sort sortBuild = null;
        if (!StringUtils.isEmpty(sort)) {
            Matcher matcher = Pattern.compile("\\[(.*)\\](.*)").matcher(sort);

            if (matcher.matches() && matcher.groupCount() == 2) {
                sortBuild = Sort.by(matcher.group(2)).ascending();

                if ("desc".equalsIgnoreCase(matcher.group(1))) {
                    sortBuild = sortBuild.descending();
                }
            }
        }

        return sortBuild;
    }

    /**
     * Get value from Http Servlet Request.
     *
     * @param key     key of value
     * @param request Http Servlet Request
     * @return value
     */
    private String getValueFromRequest(String key, HttpServletRequest request) {
        if (!StringUtils.isEmpty(key) && null != request) {
            return request.getParameter(key);
        }
        return null;
    }

    /**
     * Convert Map data to Data Transfer Object.
     *
     * @param data    map data
     * @param dtoType Data Transfer Object type
     * @return Data Transfer Object instance
     */
    @SuppressWarnings("unchecked")
    private Object convertToDataTransferObject(Object data, Class<?> dtoType) {
        if (Map.class.isAssignableFrom(data.getClass())) {
            return ObjectUtils.convertMapToObject((Map<String, ?>) data, dtoType);
        }
        throw new ParamInvalidException("Cannot parse '" + data.getClass().getName() + "' to '" + dtoType.getName() + "'");
    }

    /**
     * Get {@link HttpServletRequest} from array arguments.
     *
     * @param arguments array arguments
     * @return {@link HttpServletRequest}
     */
    private HttpServletRequest getHttpServletRequest(Object[] arguments) {
        if (null != arguments && arguments.length > 0) {
            for (Object arg : arguments) {
                if (null != arg && RequestFacade.class.isAssignableFrom(arg.getClass())) {
                    return (HttpServletRequest) arg;
                }
            }
        }
        return null;
    }
}
