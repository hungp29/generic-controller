package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.support.generic.mapping.DTOMapping;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DTOMappingCache implements Map<String, DTOMapping> {
    private LinkedHashMap<String, DTOMapping> cache = new LinkedHashMap<>();

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
        return cache.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null || value.getClass() != DTOMapping.class) return false;
        return cache.containsValue(value);
    }

    @Override
    public DTOMapping get(Object key) {
        return cache.get(key);
    }

    public DTOMapping get(Class<?> classKey) {
        return cache.get(classKey.getName());
    }

    @Override
    public DTOMapping put(String key, DTOMapping value) {
        return cache.put(key, value);
    }

    public DTOMapping put(DTOMapping value) {
        return cache.put(value.getDTOClassName(), value);
    }

    @Override
    public DTOMapping remove(Object key) {
        return cache.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends DTOMapping> m) {
        cache.putAll(m);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public Set<String> keySet() {
        return cache.keySet();
    }

    @Override
    public Collection<DTOMapping> values() {
        return cache.values();
    }

    @Override
    public Set<Entry<String, DTOMapping>> entrySet() {
        return cache.entrySet();
    }
}
