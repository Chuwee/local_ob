package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AttributeTexts implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<Long, String> name;

    private List<AttributeValue> values;

    public AttributeTexts() {
    }

    public AttributeTexts(Map<Long, String> name) {
        this.name = name;
    }

    public Map<Long, String> getName() {
        return name;
    }

    public void setName(Map<Long, String> name) {
        this.name = name;
    }

    public List<AttributeValue> getValues() {
        return values;
    }

    public void setValues(List<AttributeValue> values) {
        this.values = values;
    }
}
