package es.onebox.mgmt.datasources.ms.event.dto.products.producttickettemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateDesign;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductTicketTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = -7622321010552396800L;

    private Long id;
    private String name;
    @JsonProperty("default")
    private Boolean isDefault;
    private IdNameDTO entity;
    private TicketTemplateDesign design;

    private Long defaultLanguage;
    private List<Long> selectedLanguageIds;

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
