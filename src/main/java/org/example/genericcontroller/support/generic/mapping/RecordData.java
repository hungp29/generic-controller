package org.example.genericcontroller.support.generic.mapping;

import org.example.genericcontroller.support.generic.utils.DataTransferObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.CollectionUtils;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Record Data.
 *
 * @author hungp
 */
public class RecordData {

    private ObjectMapping objectMapping;
    private List<Map<String, Object>> records = new ArrayList<>();

    /**
     * New instance {@link RecordData}.
     *
     * @param tuples list {@link Tuple} records
     */
    private RecordData(ObjectMapping objectMapping, List<Tuple> tuples) {
        this.objectMapping = objectMapping;
        records.addAll(convertTupleToMap(tuples));
    }

    /**
     * Convert tuples to Map.
     *
     * @param tuples List {@link Tuple}
     * @return List map
     */
    private List<Map<String, Object>> convertTupleToMap(List<Tuple> tuples) {
        List<Map<String, Object>> records = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tuples)) {
            List<String> aliases = tuples.get(0).getElements()
                    .stream().map(TupleElement::getAlias).collect(Collectors.toList());
            for (Tuple tuple : tuples) {
                Map<String, Object> record = new HashMap<>();
                for (String alias : aliases) {
                    record.put(alias, tuple.get(alias));
                }
                records.add(record);
            }
        }
        return records;
    }

    public void merge(List<Tuple> tuples) {
        List<Map<String, Object>> merged = new ArrayList<>();
        List<Map<String, Object>> collection = convertTupleToMap(tuples);
        Map<String, List<Map<String, Object>>> mapCollectionById = new HashMap<>();
        if (!CollectionUtils.isEmpty(collection)) {
            for (Map<String, Object> collect : collection) {
//                String key = DataTransferObjectUtils.getKey(Constants.EMPTY_STRING, dtoType, collect);
//                List<Map<String, Object>> list = mapCollectionById.get(key);
//                if (null == list) {
//                    list = new ArrayList<>();
//                }
//                list.add(collect);
//                mapCollectionById.put(key, list);
            }
        }
        if (!CollectionUtils.isEmpty(records)) {
            for (Map<String, Object> record : records) {
                String key = DataTransferObjectUtils.getKey(Constants.EMPTY_STRING, dtoType, record);
                List<Map<String, Object>> list = mapCollectionById.get(key);
                merged.addAll(buildMergeRecord(record, list));
            }
        }
    }

    /**
     * Get list records as Map.
     *
     * @return list records
     */
    public List<Map<String, Object>> getRecords() {
        return records;
    }

    /**
     * Get size of records.
     *
     * @return size of records
     */
    public int size() {
        return records.size();
    }

    /**
     * Get condition values.
     *
     * @param key the key to get data
     * @return list condition value
     */
    public List<Object> getConditionValues(String key) {
        return records.stream().map(record -> record.get(key)).distinct().collect(Collectors.toList());
    }

    public static RecordData from(ObjectMapping objectMapping, List<Tuple> tuples) {
        return new RecordData(objectMapping, tuples);
    }

}
