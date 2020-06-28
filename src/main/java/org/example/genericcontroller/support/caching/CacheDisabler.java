package org.example.genericcontroller.support.caching;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope(proxyMode = ScopedProxyMode.NO)
public class CacheDisabler {

    private boolean disabled = false;

    public void disableCache() {
        disabled = true;
    }

    public void enableCache() {
        disabled = false;
    }

    boolean isCacheDisabled() {
        return disabled;
    }
}
