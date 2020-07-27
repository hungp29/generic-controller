package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.support.generic.utils.DataTransferObjectUtils;
import org.example.genericcontroller.support.generic.utils.MappingUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

/**
 * Implementation for DefaultRepository.
 *
 * @param <T> generic of Entity
 * @author hungp
 */
public class SimpleGenericRepository<T extends Audit> extends SimpleJpaRepository<T, Serializable> implements GenericRepository<T> {

    private final EntityManager em;
    private final GenericSpecification spec;
    private final JpaEntityInformation<T, ?> entityInformation;
    private final ObjectMappingCache mappingCache;

    /**
     * New Simple Generic Repository.
     *
     * @param entityInformation Entity information
     * @param em                {@link EntityManager} instance
     * @param spec              {@link GenericSpecification} Generic Specification instance
     * @param mappingCache      {@link ObjectMappingCache} Mapping Cache
     */
    public SimpleGenericRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager em,
                                   GenericSpecification spec, ObjectMappingCache mappingCache) {
        super(entityInformation, em);
        this.entityInformation = entityInformation;
        this.em = em;
        this.spec = spec;
        this.mappingCache = mappingCache;
    }

    /**
     * Find All.
     *
     * @param dtoType Data Transfer Object type
     * @param filter  filter field
     * @param params  request params
     * @return list Data Transfer Object
     */
    @Override
    public List<Object> findAll(Class<?> dtoType, String[] filter, Map<String, String> params, FilterData filterData) {
        TypedQuery<Tuple> query = getQuery(dtoType, filter, params, filterData, Sort.unsorted());
        List<Map<String, Object>> records = readData(query, dtoType, filter, filterData);
        return MappingUtils.convertToListDataTransferObject(records, dtoType, filter);
    }

    /**
     * Find all with pagination.
     *
     * @param dtoType  Data Transfer Object type
     * @param filter   filter fields
     * @param params   request params
     * @param pageable Paging info
     * @return page data
     */
    @Override
    public Page<Object> findAll(Class<?> dtoType, String[] filter, Map<String, String> params, FilterData filterData, Pageable pageable) {
        Page<Object> page;
        if (isUnpaged(pageable)) {
            page = new PageImpl<>(findAll(dtoType, filter, params, filterData));
        } else {
            TypedQuery<Tuple> query = getQuery(dtoType, filter, params, filterData, pageable);
            page = readPage(dtoType, filter, params, filterData, query, getDomainClass(), pageable);
        }
        return page;
    }

    /**
     * Find all entity.
     *
     * @param dtoType Data Transfer Object type
     * @param filter  filter fields
     * @param params  request params
     * @param sort    Sort instance
     * @return page data
     */
    @Override
    public Page<Object> findAll(Class<?> dtoType, String[] filter, Map<String, String> params, FilterData filterData, Sort sort) {
        TypedQuery<Tuple> query = getQuery(dtoType, filter, params, filterData, sort);
        List<Map<String, Object>> records = readData(query, dtoType, filter, filterData);
        return new PageImpl<>(MappingUtils.convertToListDataTransferObject(records, dtoType, filter));
    }

    /**
     * Get Data Transfer Object.
     *
     * @param dtoType Data Transfer Object type
     * @param filter  filter field
     * @param params  request params
     * @param sort    Sort instance
     * @return TypeQuery instance
     */
    protected TypedQuery<Tuple> getQuery(Class<?> dtoType, String[] filter, Map<String, String> params, FilterData filterData, Sort sort) {
        return getQuery(dtoType, filter, params, filterData, getDomainClass(), sort);
    }

    /**
     * Get Data Transfer Object.
     *
     * @param dtoType     Data Transfer Object type
     * @param filter      filter field
     * @param params      request params
     * @param domainClass Entity class
     * @param sort        Sort instance
     * @param <S>         generic of entity
     * @return TypeQuery instance
     */
    protected <S extends T> TypedQuery<Tuple> getQuery(Class<?> dtoType, String[] filter, Map<String, String> params, FilterData filterData,
                                                       Class<S> domainClass, Sort sort) {
        Assert.notNull(dtoType, "DTO Type must not be null!");

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);

        Root<S> root = applySpecificationToCriteria(domainClass, query, dtoType, filter, params, filterData, false);

        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return applyRepositoryMethodMetadata(em.createQuery(query));
    }

    /**
     * Get query with paging info.
     *
     * @param dtoType  Data Transfer Object type
     * @param filter   filter fields
     * @param params   request params
     * @param pageable paging info
     * @return TypedQuery instance
     */
    protected TypedQuery<Tuple> getQuery(Class<?> dtoType, String[] filter, Map<String, String> params, FilterData filterData, Pageable pageable) {
        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        return getQuery(dtoType, filter, params, filterData, getDomainClass(), sort);
    }

    /**
     * Get collection query for collection fields of entity.
     *
     * @param dtoType     Data Transfer Object type
     * @param filter      filter fields
     * @param domainClass Entity class
     * @param records     records entity
     * @param <S>         generic entity
     * @return collection query
     */
    protected <S extends T> TypedQuery<Tuple> getCollectionQuery(Class<?> dtoType, String[] filter, FilterData filterData,
                                                                 Class<S> domainClass, List<Map<String, Object>> records) {
        Assert.notNull(dtoType, "DTO Type must not be null!");

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);

        Root<S> root = applySpecificationToCriteria(domainClass, query, dtoType, filter, null, filterData, true);

//        if (sort.isSorted()) {
//            query.orderBy(toOrders(sort, root, builder));
//        }

        List<Predicate> conditions = new ArrayList<>();
        entityInformation.getIdAttributeNames().forEach(key -> {
            List<Object> conditionValue = records.stream().map(record -> record.get(key)).distinct().collect(Collectors.toList());
            conditions.add(root.get(key).in(conditionValue));
        });

        query.where(builder.and(conditions.toArray(new Predicate[0])));

        return applyRepositoryMethodMetadata(em.createQuery(query));
    }

    /**
     * Get count query.
     *
     * @param dtoType     Data Transfer Object type
     * @param domainClass entity class
     * @param params      request params
     * @param <S>         generic of entity
     * @return count query
     */
    protected <S extends T> TypedQuery<Long> getCountQuery(Class<?> dtoType, Class<S> domainClass, Map<String, String> params) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<S> root = applySpecificationToCriteria(domainClass, query, dtoType, null, params, null, false);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        // Remove all Orders the Specifications might have applied
        query.orderBy(Collections.emptyList());

        return em.createQuery(query);
    }

    /**
     * Apply Specification to criteria.
     *
     * @param domainClass Entity class
     * @param query       Criteria Query
     * @param dtoType     Data Transfer Object type
     * @param filter      filter field
     * @param params      request params
     * @param <S>         Generic of Query Type
     * @param <U>         Generic of entity
     * @return Root instance
     */
    private <S, U extends T> Root<U> applySpecificationToCriteria(Class<U> domainClass,
                                                                  CriteriaQuery<S> query, Class<?> dtoType,
                                                                  String[] filter, Map<String, String> params, FilterData filterData, boolean collection) {

        Assert.notNull(domainClass, "Domain class must not be null!");
        Assert.notNull(query, "CriteriaQuery must not be null!");

        Root<U> root = query.from(domainClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        Predicate predicate = spec.buildCriteria(root, query, builder, dtoType, filter, params, filterData, collection);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }

    /**
     * Apply repository method metadata.
     *
     * @param query Type query
     * @param <S>   Generic of Query
     * @return Type Query instance
     */
    private <S> TypedQuery<S> applyRepositoryMethodMetadata(TypedQuery<S> query) {
        CrudMethodMetadata metadata = getRepositoryMethodMetadata();
        if (metadata == null) {
            return query;
        }

        LockModeType type = metadata.getLockModeType();
        return type == null ? query : query.setLockMode(type);
    }

    /**
     * Execute count query.
     *
     * @param query count query
     * @return number of record
     */
    private static long executeCountQuery(TypedQuery<Long> query) {
        Assert.notNull(query, "TypedQuery must not be null!");

        List<Long> totals = query.getResultList();
        long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    /**
     * Check is un-page.
     *
     * @param pageable paging information
     * @return true if paging info is un-page
     */
    private static boolean isUnpaged(Pageable pageable) {
        return pageable.isUnpaged();
    }

    /**
     * Read data of one page.
     *
     * @param dtoType     Data Transfer Object type
     * @param filter      filter field
     * @param params      request params
     * @param query       query
     * @param domainClass entity class
     * @param pageable    paging info
     * @return page data
     */
    protected Page<Object> readPage(Class<?> dtoType, String[] filter, Map<String, String> params, FilterData filterData, TypedQuery<Tuple> query, final Class<T> domainClass, Pageable pageable) {

        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<Map<String, Object>> records = readData(query, dtoType, filter, filterData);
        List<Object> contents = MappingUtils.convertToListDataTransferObject(records, dtoType, filter);
        return getPage(contents, pageable, () -> executeCountQuery(getCountQuery(dtoType, domainClass, params)));
    }


    /**
     * Read data from query and get data from collection field to merge.
     *
     * @param query   TypedQuery instance
     * @param dtoType Data Transfer Object type
     * @param filter  filter field
     * @return map record
     */
    private List<Map<String, Object>> readData(TypedQuery<Tuple> query, Class<?> dtoType, String[] filter, FilterData filterData) {
        Assert.notNull(query, "TypedQuery must be not null!");
        List<Tuple> tuples = query.getResultList();
        List<Map<String, Object>> records = MappingUtils.convertTupleToMapRecord(tuples, MappingUtils.getEntityMappingFieldPaths(dtoType, filter, false));
        if (records.size() > 0) {
            List<Tuple> collectionTuples = getCollectionQuery(dtoType, filter, filterData, getDomainClass(), records).getResultList();
            List<Map<String, Object>> collectionRecords = MappingUtils.convertTupleToMapRecord(collectionTuples, MappingUtils.getEntityMappingFieldPaths(dtoType, filter, true));
            records = MappingUtils.merge(records, collectionRecords, dtoType);
        }
        return records;
    }

    /**
     * Get Page data.
     *
     * @param content       content
     * @param pageable      paging information
     * @param totalSupplier total record
     * @return page data
     */
    private static Page<Object> getPage(List<Object> content, Pageable pageable, LongSupplier totalSupplier) {
        Assert.notNull(content, "Content must not be null!");
        Assert.notNull(pageable, "Pageable must not be null!");
        Assert.notNull(totalSupplier, "TotalSupplier must not be null!");

        if (pageable.isUnpaged() || pageable.getOffset() == 0) {
            if (pageable.isUnpaged() || pageable.getPageSize() > content.size()) {
                return new PageImpl<>(content, pageable, content.size());
            }

            return new PageImpl<>(content, pageable, totalSupplier.getAsLong());
        }

        if (content.size() != 0 && pageable.getPageSize() > content.size()) {
            return new PageImpl<>(content, pageable, pageable.getOffset() + content.size());
        }

        return new PageImpl<>(content, pageable, totalSupplier.getAsLong());
    }

    @Override
    public <ID extends Serializable> Object findOneById(ID id, Class<?> dtoType, String[] filter, FilterData filterData) {
        String keyField = DataTransferObjectUtils.getFieldMappingEntityKey(dtoType);
        if (null != id && !StringUtils.isEmpty(keyField)) {
            Map<String, String> params = new HashMap<>();
            params.put(keyField, id.toString());
            TypedQuery<Tuple> query = getQuery(dtoType, filter, params, filterData, Sort.unsorted());
            List<Map<String, Object>> records = readData(query, dtoType, filter, filterData);
            List<Object> lstDTO = MappingUtils.convertToListDataTransferObject(records, dtoType, filter);
            if (!CollectionUtils.isEmpty(lstDTO)) {
                return lstDTO.get(0);
            }
        }
        return null;
    }

    /**
     * Convert Data Transfer Object to Entity and save it.
     *
     * @param dto data
     * @return entity after saved
     */
    @Override
    public T saveDataTransferObject(Object dto) {
        T entity = MappingUtils.convertDataTransferObjectToEntity(dto, getDomainClass());
        if (null != entity) {
            entity = save(entity);
        }
        return entity;
    }
}
