package es.onebox.mgmt.salerequests.pricesimulation.dto;

import es.onebox.mgmt.export.dto.BaseExportRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PriceSimulationExportRequest extends BaseExportRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty
    @Valid
    private List<PriceSimulationExportFileField> fields;

    public List<PriceSimulationExportFileField> getFields() {
        return fields;
    }

    public void setFields(
        List<PriceSimulationExportFileField> fields) {
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
