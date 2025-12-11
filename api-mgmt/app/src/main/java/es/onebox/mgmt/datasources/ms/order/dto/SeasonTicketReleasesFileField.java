package es.onebox.mgmt.datasources.ms.order.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.mgmt.export.deserializer.SeasonTicketReleasesExportFieldDeserializer;
import es.onebox.mgmt.export.dto.ExportFileField;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SeasonTicketReleasesFileField extends ExportFileField<SeasonTicketReleasesField> {

    private static final long serialVersionUID = 1L;

    @JsonDeserialize(using = SeasonTicketReleasesExportFieldDeserializer.class)
    @NotNull
    private SeasonTicketReleasesField field;

    @Override
    public SeasonTicketReleasesField getField() {
        return field;
    }

    @Override
    public void setField(SeasonTicketReleasesField field) {
        this.field = field;
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
