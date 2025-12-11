package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;
import java.util.Map;

public class AttributeValue implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Map<Long, String> value;

    public AttributeValue() {
    }

    public AttributeValue(Long id, String name, Map<Long, String> value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Long, String> getValue() {
        return value;
    }

    public void setValue(Map<Long, String> value) {
        this.value = value;
    }
}
