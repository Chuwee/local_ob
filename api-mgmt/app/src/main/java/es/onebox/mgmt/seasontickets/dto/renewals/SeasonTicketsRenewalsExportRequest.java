package es.onebox.mgmt.seasontickets.dto.renewals;

import es.onebox.mgmt.export.dto.BaseExportRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketsRenewalsExportRequest extends BaseExportRequest implements Serializable {

    private static final long serialVersionUID = -308933635314402213L;

    @NotEmpty
    @Valid
    private List<SeasonTicketRenewalsExportFileField> fields;

    public List<SeasonTicketRenewalsExportFileField> getFields() {
        return fields;
    }

    public void setFields(List<SeasonTicketRenewalsExportFileField> fields) {
        this.fields = fields;
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
