package org.example.genericcontroller.support.generic;

import lombok.extern.slf4j.Slf4j;
import org.example.genericcontroller.support.generic.exception.ConditionValueInvalidException;
import org.example.genericcontroller.support.generic.exception.WhereConditionNotSupportException;
import org.example.genericcontroller.support.generic.mapping.ObjectMapping;
import org.example.genericcontroller.support.generic.mapping.WhereCondition;
import org.example.genericcontroller.support.generic.utils.DataTransferObjectUtils;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Default Generic Specification.
 *
 * @author hungp
 */
@Slf4j
public class DefaultGenericSpecification implements GenericSpecification {

    private final ObjectMappingCache mappingCache;

    public DefaultGenericSpecification(ObjectMappingCache mappingCache) {
        this.mappingCache = mappingCache;
    }

    /**
     * Auto build criteria.
     *
     * @param root            Root Entity
     * @param query           Criteria query
     * @param criteriaBuilder Criteria builder
     * @param dtoType         Data Transfer Object type
     * @param filter          list field accepted to get from database
     * @param params          request params
     * @param filterData      contain DTO Type, Filter field and params
     * @param collection      flat to detect build criteria for collection fields
     * @param <T>             generic of entity
     * @return Predicate instance
     */
    @Override
    public <T> Predicate buildCriteria(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder,
                                       Class<?> dtoType, String[] filter, Map<String, String> params, FilterData filterData, boolean collection) {

        // Distinct if params is not null
        if (!CollectionUtils.isEmpty(filterData.getParams())) {
            query.distinct(true);
        }

        ObjectMapping objectMapping = mappingCache.getByDTOClass(filterData.getDtoType());

        // Get Selection of ObjectMapping
        List<Selection<?>> selections;
        if (!collection) {
            selections = objectMapping.getNoneCollectionSelections(root, filterData);
        } else {
            selections = objectMapping.getCollectionSelections(root, filterData);
        }
        query.multiselect(selections);

        // Build where clause
        Predicate predicate = null;
        if (!CollectionUtils.isEmpty(filterData.getParams())) {
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String> param : filterData.getParams().entrySet()) {
                String paramName = param.getKey();
                String paramValue = param.getValue();

                WhereCondition whereCondition = objectMapping.existFieldPath(root, paramName);

                if (null == whereCondition) {
                    throw new WhereConditionNotSupportException("Don't support condition field '" + paramName + "'");
                }

                if (whereCondition.isId()) {
                    buildPredicateForKey(whereCondition.getPath(), criteriaBuilder, paramValue).ifPresent(predicates::add);
                } else if (ObjectUtils.isNumber(whereCondition.getEntityField())) {
                    buildPredicateForOperator(whereCondition.getPath(), criteriaBuilder, whereCondition.getConverterType(), paramValue).ifPresent(predicates::add);
                }
            }

            predicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }
        return predicate;
    }

    /**
     * Build predicate for operator.
     *
     * @param path            criteria path
     * @param criteriaBuilder criteria builder
     * @param value           value to search
     * @return predicate
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Optional<Predicate> buildPredicateForOperator(Path path, CriteriaBuilder criteriaBuilder,
                                                                 Class<?> fieldConverterType, String value) {
        Predicate predicate = null;
        Operator operator = Operator.parse(value);

        if (null != path && null != criteriaBuilder && null != operator) {
            Object convertValue = DataTransferObjectUtils.convertField(fieldConverterType, operator.getValue());
            if (!Comparable.class.isAssignableFrom(convertValue.getClass())) {
                throw new ConditionValueInvalidException("Condition value '" + value + "' is not Comparable class");
            }
            Comparable comparableValue = (Comparable) convertValue;

            if (Operator.GREATER_THAN_OR_EQUAL_OPERATOR.equals(operator.getOperator())) {
                predicate = criteriaBuilder.greaterThanOrEqualTo(path, comparableValue);
            } else if (Operator.LESS_THAN_OR_EQUAL_OPERATOR.equals(operator.getOperator())) {
                predicate = criteriaBuilder.lessThanOrEqualTo(path, comparableValue);
            } else if (Operator.EQUAL_OPERATOR.equals(operator.getOperator())) {
                predicate = criteriaBuilder.equal(path, comparableValue);
            } else if (Operator.NOT_EQUAL_OPERATOR.equals(operator.getOperator())) {
                predicate = criteriaBuilder.notEqual(path, comparableValue);
            } else if (Operator.GREATER_THAN_OPERATOR.equals(operator.getOperator())) {
                predicate = criteriaBuilder.greaterThan(path, comparableValue);
            } else if (Operator.LESS_THAN_OPERATOR.equals(operator.getOperator())) {
                predicate = criteriaBuilder.lessThan(path, comparableValue);
            }
        }
        return Optional.ofNullable(predicate);
    }

    /**
     * Build predicate for primary key or foreign key.
     *
     * @param path            Path criteria
     * @param criteriaBuilder Criteria Builder
     * @param value           value to search
     * @return Predicate
     */
    private static Optional<Predicate> buildPredicateForKey(Path<?> path, CriteriaBuilder criteriaBuilder, String value) {
        Predicate predicate = null;
        if (null != path && null != criteriaBuilder && !StringUtils.isEmpty(value)) {
            String[] values = splitValue(value);

            if (values.length == 1) {
                predicate = criteriaBuilder.equal(path, value);
            } else {
                predicate = path.in(values);
            }
        }
        return Optional.ofNullable(predicate);
    }

    /**
     * Split value by comma.
     *
     * @param paramValue param value
     * @return array value
     */
    private static String[] splitValue(String paramValue) {
        return StringUtils.trimArrayElements(paramValue.split(Constants.COMMA));
    }
}
