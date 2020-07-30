package org.example.genericcontroller.support.generic.proxy;

import org.apache.catalina.connector.RequestFacade;
import org.example.genericcontroller.support.generic.exception.GenericException;
import org.example.genericcontroller.support.generic.exception.ParamInvalidException;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public abstract class ProcessArgument {

    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final String FILTER = "filter";
    public static final String ORDER_BY = "orderBy";
    public static final String[] NOT_PARAM_FIELDS = {PAGE, LIMIT, ORDER_BY, FILTER};

    private int defaultPage = 1;
    private int defaultLimit = 10;

    /**
     * Prepare arguments for Generic methods.
     *
     * @param args           array argument
     * @param entityType     Entity type
     * @param controllerType Controller type
     * @return array argument
     */
    public abstract Object[] prepareArguments(Object[] args, Class<?> entityType, Class<?> controllerType);

    /**
     * Get map parameters from Http Servlet Request.
     *
     * @param request Http Servlet Request
     * @return map params
     */
    protected Map<String, String> getParameters(HttpServletRequest request) {
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
    protected Pageable getPageRequest(HttpServletRequest request, Sort sort) {
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
    protected Sort getSortRequest(HttpServletRequest request) {
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
    protected String[] getFilterFields(HttpServletRequest request) {
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
    protected Object convertToDataTransferObject(Object data, Class<?> dtoType) {
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
    protected HttpServletRequest getHttpServletRequest(Object[] arguments) {
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
