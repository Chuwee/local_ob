package es.onebox.mgmt.export.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.file.exporter.status.enums.ExportStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ExportStatusResponse extends ExportResponse {

    private static final long serialVersionUID = 1L;

    private ExportStatus status;
    @JsonProperty("download_link")
    private String url;

    public ExportStatus getStatus() {
        return status;
    }

    public void setStatus(ExportStatus status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
