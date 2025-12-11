package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdDTO;

import java.io.Serializable;
import java.util.List;

public class Categories implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("allow_custom_categories")
    private Boolean allowCustomCategories;

    private List<IdDTO> selected;

    public Boolean getAllowCustomCategories() {
        return allowCustomCategories;
    }

    public void setAllowCustomCategories(Boolean allowCustomCategories) {
        this.allowCustomCategories = allowCustomCategories;
    }

    public List<IdDTO> getSelected() {
        return selected;
    }

    public void setSelected(List<IdDTO> selected) {
        this.selected = selected;
    }

}
