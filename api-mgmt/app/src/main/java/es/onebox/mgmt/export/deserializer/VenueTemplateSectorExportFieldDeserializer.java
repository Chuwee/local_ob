package es.onebox.mgmt.export.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import es.onebox.mgmt.venues.enums.VenueTemplateSectorField;

import java.io.IOException;
import java.util.Optional;

public class VenueTemplateSectorExportFieldDeserializer extends StdDeserializer<VenueTemplateSectorField> {

    private static final long serialVersionUID = 1L;

    public VenueTemplateSectorExportFieldDeserializer() {
        super(VenueTemplateSectorField.class);
    }

    @Override
    public VenueTemplateSectorField deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String code = null;
        JsonNode node = p.getCodec().readTree(p);
        if (node != null) {
            code = node.asText();
        }
        return Optional.ofNullable(VenueTemplateSectorField.getByCode(code))
                .orElseThrow(() -> new IllegalArgumentException("Unrecognized field name " + node.asText()));
    }
}
