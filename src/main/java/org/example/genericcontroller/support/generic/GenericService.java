package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.GenericException;
import org.example.genericcontroller.support.generic.utils.EntityUtils;
import org.example.genericcontroller.support.generic.utils.Validator;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default Service.
 *
 * @param <T> generic of Entity
 * @author hungp
 */
public class GenericService<T extends Audit> {

    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final String FILTER = "filter";
    public static final String ORDER_BY = "orderBy";
    public static final String[] NOT_PARAM_FIELDS = {PAGE, LIMIT, ORDER_BY, FILTER};

    private GenericRepository<T> genericRepository;

    private int defaultPage = 1;
    private int defaultLimit = 10;

    /**
     * Create and save new entity.
     *
     * @param createRequestDTO data
     * @return response instance
     */
    @Transactional
    public Object createAndSave(Object createRequestDTO) {
        @SuppressWarnings("unchecked")
        Class<T> entityClass = getEntityConfigMapping();
        return genericRepository.saveDataTransferObject(createRequestDTO);
    }

    /**
     * Get one entity data.
     *
     * @param id      id of entity
     * @param request Http Servlet Request
     * @param <ID>    generic of Id
     * @return Data Transfer Object of Entity
     */
    public <ID extends Serializable> Object get(ID id, HttpServletRequest request) {
        return null;
    }

    /**
     * Get all entity.
     *
     * @param request Http Servlet Request
     * @return Page data
     */
    public Page<Object> getAll(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        Class<T> entityClass = (Class<T>) ObjectUtils.getGenericClass(this.getClass());

        // Validate configurations of entity
        Validator.validateObjectConfiguration(entityClass, DataTransferObjectMapping.class);

        Class<?> readDTOClass = EntityUtils.getReadResponseDTO(entityClass);

        // Build page request.
        Pageable pageRequest = getPageRequest(request);

        // Get all request param
        Map<String, String> params = getParameters(request);
        // Get list filter field
        String[] filter = getFilterFields(request);

        if (!pageRequest.isUnpaged()) {
            return genericRepository.findAll(readDTOClass, filter, params, pageRequest);
        } else {
            return genericRepository.findAll(readDTOClass, filter, params, getSortRequest(request));
        }
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
     * get map parameters from Http Servlet Request.
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
    protected Pageable getPageRequest(HttpServletRequest request) {
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
        return buildPageRequest(page, limit, sortValue);
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
     * Build page request.
     *
     * @param page  page
     * @param limit limit
     * @return {@link Pageable} instance
     */
    protected Pageable buildPageRequest(Integer page, Integer limit) {
        return buildPageRequest(page, limit);
    }

    /**
     * Build page request.
     *
     * @param page      page
     * @param limit     limit
     * @param sortValue sort column and direction
     * @return {@link Pageable} instance
     */
    protected Pageable buildPageRequest(Integer page, Integer limit, String sortValue) {
        Pageable pageable = Pageable.unpaged();
        Sort sort = Sort.unsorted();
        if (!StringUtils.isEmpty(sortValue)) {
            sort = buildSort(sortValue);
        }
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

    /**
     * Get value from Http Servlet Request.
     *
     * @param key     key of value
     * @param request Http Servlet Request
     * @return value
     */
    protected String getValueFromRequest(String key, HttpServletRequest request) {
        if (!StringUtils.isEmpty(key) && null != request) {
            return request.getParameter(key);
        }
        return null;
    }

    /**
     * Get Entity class and validate entity has configuration DataTransferObjectMapping.
     *
     * @return entity type
     */
    private Class<T> getEntityConfigMapping() {
        @SuppressWarnings("unchecked")
        Class<T> entityClass = (Class<T>) ObjectUtils.getGenericClass(this.getClass());

        // Validate configurations of entity
        Validator.validateObjectConfiguration(entityClass, DataTransferObjectMapping.class);
        return entityClass;
    }

    @Autowired
    public void setGenericRepository(GenericRepository<T> genericRepository) {
        this.genericRepository = genericRepository;
    }
}
