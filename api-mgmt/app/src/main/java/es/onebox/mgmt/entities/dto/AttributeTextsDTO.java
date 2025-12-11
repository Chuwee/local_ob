package es.onebox.mgmt.entities.dto;

import java.util.List;
import java.util.Map;

public class AttributeTextsDTO {

    private Map<String, String> name;

    private List<AttributeValueDTO> values;

    public AttributeTextsDTO() {
    }

    public AttributeTextsDTO(Map<String, String> name) {
        this.name = name;
    }

    public AttributeTextsDTO(Map<String, String> name, List<AttributeValueDTO> values) {
        this.name = name;
        this.values = values;
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public List<AttributeValueDTO> getValues() {
        return values;
    }

    public void setValues(List<AttributeValueDTO> values) {
        this.values = values;
    }
}
