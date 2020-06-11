package org.example.genericcontroller.support.generic;

import org.springframework.lang.Nullable;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

public interface SelectionCriteria<T> {

    Selection<?>[] buildMultiSelect(Root<T> root, Class<?> dtoType, @Nullable String[] filter);
}
