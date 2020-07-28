package org.example.genericcontroller.support.generic.mapping;

import org.example.genericcontroller.utils.constant.Constants;

public class PrefixPath {

    private String entityPrefix;
    private String dtoPrefix;

    private PrefixPath(String entityPrefix, String dtoPrefix) {
        this.entityPrefix = entityPrefix;
        this.dtoPrefix = dtoPrefix;
    }

    public String getEntityPrefix() {
        return entityPrefix;
    }

    public String getDtoPrefix() {
        return dtoPrefix;
    }

    public static class PrefixPathBuilder {
        private String entityPrefix = "";
        private String dtoPrefix = "";

        public PrefixPathBuilder(PrefixPath prefixPath) {
            if (null != prefixPath) {
                this.entityPrefix = prefixPath.entityPrefix;
                this.dtoPrefix = prefixPath.dtoPrefix;
            }
        }

        public PrefixPathBuilder add(FieldMapping fieldMapping) {
            for (GenericField field : fieldMapping.getEntityFieldAsQueue()) {
                entityPrefix += field.getFieldName() + Constants.DOT;
            }
            dtoPrefix += fieldMapping.getDTOField().getFieldName() + Constants.DOT;
            return this;
        }

        public PrefixPath build() {
            return new PrefixPath(entityPrefix, dtoPrefix);
        }
    }
}
