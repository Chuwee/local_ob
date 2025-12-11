package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.mgmt.export.deserializer.VenueTemplateViewExportFieldDeserializer;
import es.onebox.mgmt.export.dto.ExportFileField;
import es.onebox.mgmt.venues.enums.VenueTemplateViewField;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class VenueTemplatesViewExportFileField extends ExportFileField<VenueTemplateViewField> {

    @JsonDeserialize(using = VenueTemplateViewExportFieldDeserializer.class)
    @NotNull
    private VenueTemplateViewField fields;

    @Override
    public VenueTemplateViewField getField() {
        return fields;
    }

    @Override
    public void setField(VenueTemplateViewField fields) {
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

}
