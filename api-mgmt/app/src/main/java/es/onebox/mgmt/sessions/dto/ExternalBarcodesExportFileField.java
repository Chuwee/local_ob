package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.mgmt.export.deserializer.ExternalBarcodesExportFieldDeserializer;
import es.onebox.mgmt.export.dto.ExportFileField;
import es.onebox.mgmt.sessions.enums.ExternalBarcodesField;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class ExternalBarcodesExportFileField extends ExportFileField<ExternalBarcodesField> {

    @JsonDeserialize(using = ExternalBarcodesExportFieldDeserializer.class)
    @NotNull
    private ExternalBarcodesField field;

    @Override
    public ExternalBarcodesField getField() {
        return field;
    }

    @Override
    public void setField(ExternalBarcodesField field) {
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
