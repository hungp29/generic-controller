package org.example.genericcontroller.support.generic;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.genericcontroller.utils.constant.Constants;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Search Extractor.
 *
 * @author hungp
 */
@Slf4j
@Data
public class RootFilterData {

    private Class<?> dtoType;
    private String[] filter;
    private Map<String, String> params;

    /**
     * Purpose for app new instance in controller.
     */
    public RootFilterData() {
        log.debug("Controller create Filter Data object");
    }

    /**
     * New instance.
     *
     * @param dtoType DTO Type
     * @param filter
     * @param params
     */
    public RootFilterData(Class<?> dtoType, String[] filter, Map<String, String> params) {
        this.dtoType = dtoType;
        this.filter = merge(filter);
        this.params = params;
    }

    /**
     * Merge filter fields.
     *
     * @param filterArray filter fields in array
     * @return filter fields after merged
     */
    private String[] merge(String[] filterArray) {
        if (null != filterArray && filterArray.length > 1) {
            List<String> filter = Arrays.stream(filterArray)
                    .sorted(Comparator.comparingInt(String::length))
                    .collect(Collectors.toList());
            int first = 0;
            while (first < filter.size()) {
                int seconds = first + 1;
                while (seconds < filter.size()) {
                    if (filter.get(seconds).equals(filter.get(first)) ||
                            filter.get(seconds).startsWith(filter.get(first).concat(Constants.DOT))) {
                        filter.remove(seconds);
                    } else if (filter.get(first).startsWith(filter.get(seconds).concat(Constants.DOT))) {
                        filter.set(first, filter.get(seconds));
                        filter.remove(seconds);
                    } else {
                        seconds++;
                    }
                }
                first++;
            }
            filterArray = filter.toArray(new String[0]);
        }
        return filterArray;
    }

    /**
     * News instance {@link FilterData} from {@link RootFilterData}.
     *
     * @return {@link FilterData} instance
     */
    public FilterData toFilterData() {
        return new FilterData(dtoType, filter, params);
    }

    /**
     * News instance {@link FilterData} from {@link RootFilterData}.
     *
     * @return {@link FilterData} instance
     */
    public FilterData toCollectionFilterData() {
        return new FilterData(dtoType, filter, null);
    }

    /**
     * News instance {@link FilterData} from {@link RootFilterData}.
     *
     * @return {@link FilterData} instance
     */
    public FilterData toCountFilterData() {
        return new FilterData(dtoType, null, params);
    }

    @Getter
    public static class FilterData {
        private Class<?> dtoType;
        private String[] filter;
        private Map<String, String> params;

        private FilterData(Class<?> dtoType, String[] filter, Map<String, String> params) {
            this.dtoType = dtoType;
            this.filter = filter;
            this.params = params;
        }
    }
}
