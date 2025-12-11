package es.onebox.event.tickettemplates.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serializable;
import java.util.List;

public class TicketTemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    @JsonProperty("default")
    private Boolean isDefault;
    private IdNameDTO entity;
    private Boolean excludeBarcode;
    private TicketTemplateDesignDTO design;

    private Integer defaultLanguage;
    private List<Integer> selectedLanguageIds;

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

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public Boolean getExcludeBarcode() {
        return excludeBarcode;
    }

    public void setExcludeBarcode(Boolean excludeBarcode) {
        this.excludeBarcode = excludeBarcode;
    }

    public TicketTemplateDesignDTO getDesign() {
        return design;
    }

    public void setDesign(TicketTemplateDesignDTO design) {
        this.design = design;
    }

    public Integer getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(Integer defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public List<Integer> getSelectedLanguageIds() {
        return selectedLanguageIds;
    }

    public void setSelectedLanguageIds(List<Integer> selectedLanguageIds) {
        this.selectedLanguageIds = selectedLanguageIds;
    }
}
