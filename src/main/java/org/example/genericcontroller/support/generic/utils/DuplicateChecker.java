package org.example.genericcontroller.support.generic.utils;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import java.util.Set;

/**
 * Duplicate checker.
 *
 * @author hungp
 */
public class DuplicateChecker {

    /**
     * Prevent new instance.
     */
    private DuplicateChecker() {
    }

    /**
     * Get exist join.
     *
     * @param join       root join
     * @param entityType entity type
     * @param <X>        generic of left side of join
     * @param <Y>        generic of right side of join
     * @return right side of join if it exist
     */
    public static <X, Y> Join<Y, ?> existJoin(From<X, Y> join, Class<?> entityType) {
        if (null != join && null != entityType) {
            Set<Join<Y, ?>> joinSet = join.getJoins();
            for (Join<Y, ?> joinChecking : joinSet) {
                if (entityType.equals(joinChecking.getModel().getBindableJavaType())) {
                    return joinChecking;
                }
            }
        }
        return null;
    }
}
