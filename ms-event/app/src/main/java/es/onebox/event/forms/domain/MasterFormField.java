package es.onebox.event.forms.domain;

import java.io.Serializable;
import java.util.List;

public class MasterFormField implements Serializable {

    private String key;
    private FieldType type;
    private Integer size;
    private ValidationType validationType;
    private List<FieldValue> values;
    private List<MasterFormSubField> fields;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<FieldValue> getValues() {
        return values;
    }

    public void setValues(List<FieldValue> values) {
        this.values = values;
    }

    public ValidationType getValidationType() {
        return validationType;
    }

    public void setValidationType(ValidationType validationType) {
        this.validationType = validationType;
    }

    public List<MasterFormSubField> getFields() {
        return fields;
    }

    public void setFields(List<MasterFormSubField> fields) {
        this.fields = fields;
    }
} 