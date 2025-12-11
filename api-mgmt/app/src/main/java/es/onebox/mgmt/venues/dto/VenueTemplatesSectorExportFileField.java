package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.mgmt.export.deserializer.VenueTemplateSectorExportFieldDeserializer;
import es.onebox.mgmt.export.dto.ExportFileField;
import es.onebox.mgmt.venues.enums.VenueTemplateSectorField;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class VenueTemplatesSectorExportFileField extends ExportFileField<VenueTemplateSectorField> {

    @JsonDeserialize(using = VenueTemplateSectorExportFieldDeserializer.class)
    @NotNull
    private VenueTemplateSectorField fields;

    @Override
    public VenueTemplateSectorField getField() {
        return fields;
    }

    @Override
    public void setField(VenueTemplateSectorField field) {
        this.fields = field;
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
