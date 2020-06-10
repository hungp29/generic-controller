package org.example.genericcontroller.support.defaulthttp;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.Set;

public class JoinChecker {

    private JoinChecker() {
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
