package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.support.generic.mapping.DTOMapping;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.Entity;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DTOMappingCache {
    private LinkedHashMap<String, DTOMapping> cache = new LinkedHashMap<>();
    private HashMap<String, List<String>> mapKey = new HashMap<>();

    public int size() {
        return cache.size();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public boolean containsDTOKey(String key) {
        return cache.containsKey(key);
    }

    public boolean containsEntityKey(String key) {
        return mapKey.containsKey(key);
    }

    public boolean containsValue(DTOMapping value) {
        return cache.containsValue(value);
    }

    public DTOMapping getByDTOKey(String key) {
        return cache.get(key);
    }

    public DTOMapping getByDTOClass(Class<?> classType) {
        if (AnnotatedElementUtils.hasAnnotation(classType, MappingClass.class)) {
            return getByDTOKey(classType.getName());
        }
        return null;
    }

    public List<DTOMapping> getByEntityKey(String key) {
        List<DTOMapping> result = new LinkedList<>();
        List<String> keys = mapKey.get(key);
        if (!CollectionUtils.isEmpty(keys)) {
            keys.forEach(k -> result.add(cache.get(k)));
        }
        return result;
    }

    public List<DTOMapping> getByEntityClass(Class<?> classType) {
        if (AnnotatedElementUtils.hasAnnotation(classType, Entity.class)) {
            return getByEntityKey(classType.getName());
        }
        return null;
    }

    public DTOMapping put(DTOMapping value) {
        String dtoKey = value.getDTOClassName();
        cache.put(dtoKey, value);

        String entityKey = value.getEntityClassName();
        List<String> keys = mapKey.get(entityKey);
        if (null == keys) {
            keys = new LinkedList<>();
        }
        keys.add(dtoKey);
        mapKey.put(entityKey, keys);
        return null;
    }

    public DTOMapping removeByDTOKey(String key) {
        DTOMapping value = cache.remove(key);
        if (null != value) {
            List<String> keys = mapKey.get(value.getEntityClassName());
            keys.remove(key);
        }
        return value;
    }

    public void clear() {
        cache.clear();
        mapKey.clear();
    }

    public Set<String> keySetDTO() {
        return cache.keySet();
    }

    public Set<String> keySetEntity() {
        return mapKey.keySet();
    }

    public Collection<DTOMapping> values() {
        return cache.values();
    }
}
