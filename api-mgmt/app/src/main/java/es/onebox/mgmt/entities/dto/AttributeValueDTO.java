package es.onebox.mgmt.entities.dto;

import java.util.Map;

public class AttributeValueDTO {

    private Long id;
    private String name;
    private Map<String, String> value;

    public AttributeValueDTO() {
    }

    public AttributeValueDTO(Long id, String name, Map<String, String> value) {
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

    public Map<String, String> getValue() {
        return value;
    }

    public void setValue(Map<String, String> value) {
        this.value = value;
    }
}
