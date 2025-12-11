package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.common.CommunicationElementTextDTO;
import es.onebox.mgmt.templateszones.dto.TemplateZonesWhitelabelSettingsDTO;
import es.onebox.mgmt.templateszones.enums.TemplatesZonesStatus;
import es.onebox.mgmt.templateszones.enums.TemplatesZonesTagType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class TemplatesZonesUpdateRequest {

    private String name;
    private TemplatesZonesStatus status;
    private List<CommunicationElementTextDTO<TemplatesZonesTagType>> contentsTexts;
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
}
