package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.mgmt.export.deserializer.WhitelistExportFieldDeserializer;
import es.onebox.mgmt.export.dto.ExportFileField;
import es.onebox.mgmt.sessions.enums.WhiteListField;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class WhiteListExportFileField extends ExportFileField<WhiteListField> {

    @JsonDeserialize(using = WhitelistExportFieldDeserializer.class)
    @NotNull
    private WhiteListField field;

    @Override
    public WhiteListField getField() {
        return field;
    }

    @Override
    public void setField(WhiteListField field) {
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
