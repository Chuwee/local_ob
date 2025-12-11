package es.onebox.mgmt.seasontickets.dto.renewals;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.mgmt.export.deserializer.SeasonTicketRenewalsExportFileFieldDeserializer;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketRenewalsFileField;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SeasonTicketRenewalsExportFileField implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonDeserialize(using = SeasonTicketRenewalsExportFileFieldDeserializer.class)
    @NotNull
    private SeasonTicketRenewalsFileField field;

    private String name;

    public SeasonTicketRenewalsFileField getField() {
        return field;
    }

    public void setField(SeasonTicketRenewalsFileField field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
