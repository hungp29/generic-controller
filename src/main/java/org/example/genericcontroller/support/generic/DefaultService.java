package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.GenericException;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default Service.
 *
 * @param <T> generic of Entity
 * @author hungp
 */
public class DefaultService<T extends Audit> {

    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final String FILTER = "filter";
    public static final String ORDER_BY = "orderBy";
    public static final String[] NOT_PARAM_FIELDS = {PAGE, LIMIT, ORDER_BY, FILTER};

    private DefaultRepository<T> defaultRepository;

    private int defaultPage = 1;
    private int defaultLimit = 10;


    public <ID extends Serializable> Object getEntity(ID id, HttpServletRequest request) {
        return null;
    }

    public List<Object> getAllEntity(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        Class<T> entityClass = (Class<T>) ObjectUtils.getGenericClass(this.getClass());
        Class<?> readDTOClass;

        // Validate configurations of entity
        Validator.validateObjectConfiguration(entityClass, DataTransferObjectMapping.class);

        readDTOClass = ObjectUtils.getAnnotation(entityClass, DataTransferObjectMapping.class).forRead();

        // Validate configurations of dto
        Validator.validateObjectConfiguration(readDTOClass, MappingClass.class);

        // Build page request.
        PageRequest pageRequest = getPageRequest(request);
        // Get all request param
        Map<String, String> params = getParameters(request);
        // Get list filter field
        String[] filter = getFilterFields(request);

        return defaultRepository.findAll(readDTOClass, filter, DefaultGenericSpecification.autoBuildSpecification());
    }

    protected String[] getFilterFields(HttpServletRequest request) {
        String filterFieldsValue = request.getParameter(FILTER);
        if (!StringUtils.isEmpty(filterFieldsValue)) {
            return StringUtils.trimArrayElements(filterFieldsValue.split(Constants.COMMA));
        }
        return null;
    }

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

    protected PageRequest getPageRequest(HttpServletRequest request) {
        PageRequest pageRequest = null;
        String page = getValueFromRequest(PAGE, request);
        String limit = getValueFromRequest(LIMIT, request);
        String orderBy = getValueFromRequest(ORDER_BY, request);
        try {
            if (!StringUtils.isEmpty(page) && !StringUtils.isEmpty(limit)) {
                if (StringUtils.isEmpty(orderBy)) {
                    pageRequest = buildPageRequest(Integer.parseInt(page), Integer.parseInt(limit));
                } else {
                    pageRequest = buildPageRequest(Integer.parseInt(page), Integer.parseInt(limit), orderBy);
                }
            }
        } catch (NumberFormatException e) {
            throw new GenericException("Cannot convert value for paging");
        }
        return pageRequest;
    }

    protected PageRequest buildPageRequest(Integer page, Integer limit) {
        page = page == null ? 0 : page - this.defaultPage;
        limit = limit == null ? this.defaultLimit : limit;
        return PageRequest.of(page, limit);
    }

    protected PageRequest buildPageRequest(Integer page, Integer limit, String sort) {
        if (StringUtils.isEmpty(sort)) {
            return buildPageRequest(page, limit);
        } else {
            page = page == null ? 0 : page - this.defaultPage;
            limit = limit == null ? this.defaultLimit : limit;
            return PageRequest.of(page, limit, buildSort(sort));
        }
    }

    protected Sort buildSort(String sort) {
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

    protected String getValueFromRequest(String key, HttpServletRequest request) {
        if (!StringUtils.isEmpty(key) && null != request) {
            return request.getParameter(key);
        }
        return null;
    }

    @Autowired
    public void setDefaultRepository(DefaultRepository<T> defaultRepository) {
        this.defaultRepository = defaultRepository;
    }
}
