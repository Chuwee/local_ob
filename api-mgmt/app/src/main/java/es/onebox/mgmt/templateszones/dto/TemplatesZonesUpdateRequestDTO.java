package es.onebox.mgmt.templateszones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.CommunicationElementTextDTO;
import es.onebox.mgmt.templateszones.enums.TemplatesZonesStatus;
import es.onebox.mgmt.templateszones.enums.TemplatesZonesTagType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class TemplatesZonesUpdateRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private TemplatesZonesStatus status;
    @JsonProperty("contents_texts")
    private List<CommunicationElementTextDTO<TemplatesZonesTagType>> contentsTexts;
    @JsonProperty("whitelabel_settings")
    private TemplateZonesWhitelabelSettingsDTO whitelabelSettings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TemplatesZonesStatus getStatus() {
        return status;
    }

    public void setStatus(TemplatesZonesStatus status) {
        this.status = status;
    }

    public List<CommunicationElementTextDTO<TemplatesZonesTagType>> getContentsTexts() {
        return contentsTexts;
    }

    public void setContentsTexts(List<CommunicationElementTextDTO<TemplatesZonesTagType>> contentsTexts) {
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
