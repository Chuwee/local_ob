package es.onebox.mgmt.salerequests.pricesimulation.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.mgmt.export.deserializer.PriceSimulationExportFileFieldDeserializer;
import es.onebox.mgmt.salerequests.pricesimulation.dto.enums.PriceSimulationFileField;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class PriceSimulationExportFileField implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonDeserialize(using = PriceSimulationExportFileFieldDeserializer.class)
    @NotNull
    private PriceSimulationFileField field;

    private String name;

    public PriceSimulationFileField getField() {
        return field;
    }

    public void setField(PriceSimulationFileField field) {
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
