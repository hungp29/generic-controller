package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.support.generic.mapping.DTOMapping;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DTOMappingCache implements Map<List<String>, DTOMapping> {
    private LinkedHashMap<List<String>, DTOMapping> cache = new LinkedHashMap<>();

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return cache.keySet().stream().flatMap(Collection::stream).anyMatch(k -> k.equals(key));
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null || value.getClass() != DTOMapping.class) return false;
        return cache.containsValue(value);
    }

    @Override
    public DTOMapping get(Object key) {
        return cache.entrySet()
                .stream()
                .filter(listDTOMappingEntry -> listDTOMappingEntry.getKey().contains(key))
                .map(Entry::getValue).findFirst().orElse(null);
    }

    public DTOMapping get(Class<?> classKey) {
        return get(classKey.getName());
    }

    @Override
    public DTOMapping put(List<String> key, DTOMapping value) {
        return cache.put(key, value);
    }

    public DTOMapping put(DTOMapping value) {
        return put(Arrays.asList(value.getDTOClassName(), value.getEntityClassName()), value);
    }

    @Override
    public DTOMapping remove(Object key) {
        return cache.remove(key);
    }

    @Override
    public void putAll(Map<? extends List<String>, ? extends DTOMapping> m) {
        cache.putAll(m);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public Set<List<String>> keySet() {
        return cache.keySet();
    }

    @Override
    public Collection<DTOMapping> values() {
        return cache.values();
    }

    @Override
    public Set<Entry<List<String>, DTOMapping>> entrySet() {
        return cache.entrySet();
    }
}
