package es.onebox.mgmt.sessions.dto;

import es.onebox.mgmt.export.dto.ExportRequest;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class ExternalBarcodesExportRequestDTO extends ExportRequest<ExternalBarcodesExportFileField> {

    private static final long serialVersionUID = 1L;

    private List<String> barcodes;

    public List<String> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<String> barcodes) {
        this.barcodes = barcodes;
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
