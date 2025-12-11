package es.onebox.event.forms.domain;

import java.io.Serializable;
import java.util.List;

public class MasterFormSubField implements Serializable {

    private String key;
    private FieldType type;
    private Integer size;
    private List<FieldValue> values;

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
} 