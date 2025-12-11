package es.onebox.mgmt.datasources.ms.ticket.dto;

import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.mgmt.sessions.dto.CapacityExportFileField;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class CapacityExportFilter extends ExportFilter<CapacityExportFileField>  {

    private Long sessionId;
    private Long venueTemplateId;
    private List<Long> sectorIds;
    private List<Long> viewIds;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Long venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public List<Long> getSectorIds() {
        return sectorIds;
    }

    public void setSectorIds(List<Long> sectorIds) {
        this.sectorIds = sectorIds;
    }

    public List<Long> getViewIds() {
        return viewIds;
    }

    public void setViewIds(List<Long> viewIds) {
        this.viewIds = viewIds;
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
