package es.onebox.event.catalog.dto;

import java.io.Serial;
import java.util.List;
import java.util.Map;

import es.onebox.event.sessions.dto.DynamicPriceTranslationDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;

public class CatalogPriceZoneOccupationDTO extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = -4808206747687815504L;

    private Map<TicketStatus, Long> status;
    private List<DynamicPriceTranslationDTO> dynamicPriceTranslations;
    private Boolean unlimited;
    private Long limit;
    private Map<String, Object> additionalProperties;

    
    public Map<TicketStatus, Long> getStatus() {
        return status;
    }

    public void setStatus(Map<TicketStatus, Long> status) {
        this.status = status;
    }

    public Boolean getUnlimited() {
        return unlimited;
    }

    public void setUnlimited(Boolean unlimited) {
        this.unlimited = unlimited;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public List<DynamicPriceTranslationDTO> getDynamicPriceTranslations() {
        return dynamicPriceTranslations;
    }

    public void setDynamicPriceTranslations(List<DynamicPriceTranslationDTO> dynamicPriceTranslations) {
        this.dynamicPriceTranslations = dynamicPriceTranslations;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
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
