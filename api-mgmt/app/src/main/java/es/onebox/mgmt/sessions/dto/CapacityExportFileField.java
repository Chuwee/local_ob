package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.mgmt.export.deserializer.SessionCapacityExportFieldDeserializer;
import es.onebox.mgmt.export.dto.ExportFileField;
import es.onebox.mgmt.sessions.enums.SessionCapacityField;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class CapacityExportFileField extends ExportFileField<SessionCapacityField> {

    @JsonDeserialize(using = SessionCapacityExportFieldDeserializer.class)
    @NotNull
    private SessionCapacityField field;

    @Override
    public SessionCapacityField getField() {
        return field;
    }

    @Override
    public void setField(SessionCapacityField field) {
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
}
