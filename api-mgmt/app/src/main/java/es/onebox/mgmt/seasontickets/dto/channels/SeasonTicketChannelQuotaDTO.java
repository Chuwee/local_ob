package es.onebox.mgmt.seasontickets.dto.channels;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SeasonTicketChannelQuotaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String description;
    @JsonProperty("template_name")
    private String templateName;
    private Boolean selected;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
