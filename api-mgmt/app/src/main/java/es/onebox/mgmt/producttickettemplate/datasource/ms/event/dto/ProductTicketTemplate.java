package es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateDesign;

public class ProductTicketTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    @JsonProperty("default")
    private Boolean isDefault;
    private IdNameDTO entity;
    private TicketTemplateDesign design;

    private Long defaultLanguage;
    private List<Long> selectedLanguageIds;

    public ProductTicketTemplate(Long id, String name, Boolean isDefault, IdNameDTO entity,
                                 TicketTemplateDesign design, Long defaultLanguage, List<Long> selectedLanguageIds) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
        this.entity = entity;
        this.design = design;
        this.defaultLanguage = defaultLanguage;
        this.selectedLanguageIds = selectedLanguageIds;
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

    public TicketTemplateDesign getDesign() {
        return design;
    }

    public void setDesign(TicketTemplateDesign design) {
        this.design = design;
    }

    public Long getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(Long defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public List<Long> getSelectedLanguageIds() {
        return selectedLanguageIds;
    }

    public void setSelectedLanguageIds(List<Long> selectedLanguageIds) {
        this.selectedLanguageIds = selectedLanguageIds;
    }
}
