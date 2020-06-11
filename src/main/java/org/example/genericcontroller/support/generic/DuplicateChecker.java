package org.example.genericcontroller.support.generic;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import java.util.Set;

public class DuplicateChecker {

    private DuplicateChecker() {
    }

    public static <X, Y> Join<Y, ?> existJoin(From<X, Y> root, Class<?> entityType) {
        Join<Y, ?> join = null;
        if (null != root && null != entityType) {
            Set<Join<Y, ?>> joinSet = root.getJoins();
            for (Join<Y, ?> joinChecking : joinSet) {
                if (entityType.equals(joinChecking.getModel().getBindableJavaType())) {
                    join = joinChecking;
                    break;
                }
            }
        }
        return join;
    }
}
