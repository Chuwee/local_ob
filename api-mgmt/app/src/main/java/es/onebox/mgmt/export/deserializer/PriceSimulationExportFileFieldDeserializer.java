package es.onebox.mgmt.export.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import es.onebox.mgmt.salerequests.pricesimulation.dto.PriceSimulationExportFileField;
import es.onebox.mgmt.salerequests.pricesimulation.dto.enums.PriceSimulationFileField;

import java.io.IOException;
import java.io.Serial;
import java.util.Optional;

public class PriceSimulationExportFileFieldDeserializer extends
    StdDeserializer<PriceSimulationFileField> {

    @Serial
    private static final long serialVersionUID = 1L;

    public PriceSimulationExportFileFieldDeserializer() {
        super(PriceSimulationExportFileField.class);
    }

    @Override
    public PriceSimulationFileField deserialize(JsonParser p, DeserializationContext ctx)
        throws IOException {
        String code = null;
        JsonNode node = p.getCodec().readTree(p);
        if (node != null) {
            code = node.asText();
        }
        return Optional.ofNullable(PriceSimulationFileField.getByCode(code))
            .orElseThrow(
                () -> {
                    assert node != null;
                    return new IllegalArgumentException("Unrecognized field name " + node.asText());
                });
    }
}
