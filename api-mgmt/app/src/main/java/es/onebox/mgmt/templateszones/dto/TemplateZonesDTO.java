package es.onebox.mgmt.templateszones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.CommunicationElementTextDTO;
import es.onebox.mgmt.templateszones.enums.TemplatesZonesStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class TemplateZonesDTO<T extends Serializable> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String code;
    private TemplatesZonesStatus status;
    @JsonProperty("contents_texts")
    private List<CommunicationElementTextDTO<T>> contentsTexts;
    @JsonProperty("whitelabel_settings")
    private TemplateZonesWhitelabelSettingsDTO whitelabelSettings;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TemplatesZonesStatus getStatus() {
        return status;
    }

    public void setStatus(TemplatesZonesStatus status) {
        this.status = status;
    }

    public List<CommunicationElementTextDTO<T>> getContentsTexts() {
        return contentsTexts;
    }

    public void setContentsTexts(List<CommunicationElementTextDTO<T>> contentsTexts) {
        this.contentsTexts = contentsTexts;
    }

    public TemplateZonesWhitelabelSettingsDTO getWhitelabelSettings() {
        return whitelabelSettings;
    }

    public void setWhitelabelSettings(TemplateZonesWhitelabelSettingsDTO whitelabelSettings) {
        this.whitelabelSettings = whitelabelSettings;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
