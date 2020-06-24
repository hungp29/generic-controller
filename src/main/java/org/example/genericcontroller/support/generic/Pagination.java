package org.example.genericcontroller.support.generic;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Pagination.
 *
 * @author hungp
 */
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {

    private Pageable pageable;

    private Sort sort;

    /**
     * Check pagination is un-paged.
     *
     * @return true if {@link Pageable} is null or un-paged
     */
    public boolean isUnPaged() {
        return null == pageable || pageable.isUnpaged();
    }

    /**
     * Get Pageable.
     *
     * @return {@link Pageable} instance if pageable not null, otherwise return Pageable.unpaged()
     */
    public Pageable getPageable() {
        if (null != pageable) {
            return pageable;
        }
        return Pageable.unpaged();
    }

    /**
     * Get Sort.
     *
     * @return {@link Sort} instance if sort not null, otherwise return Sort.unsorted()
     */
    public Sort getSort() {
        if (null != sort) {
            return sort;
        }
        return Sort.unsorted();
    }
}
