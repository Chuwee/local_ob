package es.onebox.event.attributes;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class AttributeRequestValueDTO {

    @NotNull
    private Long id;
    private String value;
    private List<Long> selected;

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

}
