package es.onebox.event.attributes;

import java.io.Serializable;
import java.util.List;

public class AttributeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String value;
    private List<Long> selected;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Long> getSelected() {
        return selected;
    }

    public void setSelected(List<Long> selected) {
        this.selected = selected;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

