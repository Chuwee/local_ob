package es.onebox.mgmt.collectives.collectivecodes.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.mgmt.collectives.collectivecodes.enums.CollectiveCodeField;
import es.onebox.mgmt.export.deserializer.CollectiveCodeExportFieldDeserializer;
import es.onebox.mgmt.export.dto.ExportFileField;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class CollectiveCodeExportFileField extends ExportFileField<CollectiveCodeField> {

    @JsonDeserialize(using = CollectiveCodeExportFieldDeserializer.class)
    @NotNull
    private CollectiveCodeField field;

    @Override
    public CollectiveCodeField getField() {
        return field;
    }

    @Override
    public void setField(CollectiveCodeField field) {
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
