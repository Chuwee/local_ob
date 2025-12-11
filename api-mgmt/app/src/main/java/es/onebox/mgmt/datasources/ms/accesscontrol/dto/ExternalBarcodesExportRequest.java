package es.onebox.mgmt.datasources.ms.accesscontrol.dto;

import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.mgmt.sessions.dto.ExternalBarcodesExportFileField;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class ExternalBarcodesExportRequest extends ExportFilter<ExternalBarcodesExportFileField> {


    private static final long serialVersionUID = 1L;

    private List<String> barcodes;

    private Long eventId;

    private Long sessionId;

    public List<String> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<String> barcodes) {
        this.barcodes = barcodes;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
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
