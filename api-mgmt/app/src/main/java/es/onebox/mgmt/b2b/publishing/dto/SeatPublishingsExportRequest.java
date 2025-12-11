package es.onebox.mgmt.b2b.publishing.dto;

import es.onebox.mgmt.export.dto.BaseExportRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

public class SeatPublishingsExportRequest extends BaseExportRequest implements Serializable {

    private static final long serialVersionUID = -308933635314402213L;

    @NotEmpty
    @Valid
    private List<SeatPublishingExportFileField> fields;

    public List<SeatPublishingExportFileField> getFields() {
        return fields;
    }

    public void setFields(List<SeatPublishingExportFileField> fields) {
        this.fields = fields;
    }
}